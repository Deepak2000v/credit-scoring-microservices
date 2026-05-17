package com.ms.credit.service;

import com.ms.credit.client.UserManagementClient;
import com.ms.credit.dto.CreditScoreDTO;
import com.ms.credit.dto.FinancialDataDTO;
import com.ms.credit.entity.CreditScore;
import com.ms.credit.entity.FinancialRecord;
import com.ms.credit.repository.CreditScoreRepository;
import com.ms.credit.repository.FinancialRecordRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditScoreService {

    // Log4j2 logger - used to log to console, file, and Splunk
    private static final Logger logger = LogManager.getLogger(CreditScoreService.class);

    private static final String KAFKA_TOPIC = "credit-score-updates";

    @Autowired
    private CreditScoreRepository creditScoreRepository;

    @Autowired
    private FinancialRecordRepository financialRecordRepository;

    // Redis cache template for storing/retrieving credit scores
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Kafka template for publishing credit score update events
    @Autowired
    private KafkaTemplate<String, FinancialDataDTO> kafkaTemplate;

    @Autowired
    private UserManagementClient userManagementClient;

    /**
     * Calculate and save a new credit score.
     * Also stores result in Redis cache and publishes Kafka event for email notification.
     *
     * @param creditScoreDTO DTO containing userId, emailId, score, scoreType, algorithmUsed
     * @return saved CreditScore entity
     */
    public CreditScore calculateCreditScore(CreditScoreDTO creditScoreDTO) {
        logger.info("Calculating credit score for userId: {}", creditScoreDTO.getUserId());

        // Convert DTO to entity and save to MySQL
        CreditScore creditScore = convertToEntity(creditScoreDTO);
        CreditScore saved = creditScoreRepository.save(creditScore);

        // Store result in Redis cache (key = emailId, value = CreditScore entity)
        redisTemplate.opsForValue().set(creditScoreDTO.getEmailId(), saved);
        logger.info("Credit score cached in Redis for: {}", creditScoreDTO.getEmailId());

        // Publish Kafka event so KafkaConsumerConfig triggers email notification
        FinancialDataDTO kafkaMessage = new FinancialDataDTO();
        kafkaMessage.setUserId(creditScoreDTO.getUserId());
        kafkaMessage.setEmailId(creditScoreDTO.getEmailId());
        kafkaMessage.setCreditScore(creditScoreDTO.getScore());
        kafkaTemplate.send(KAFKA_TOPIC, kafkaMessage);
        logger.info("Kafka event published to topic '{}' for userId: {}", KAFKA_TOPIC, creditScoreDTO.getUserId());

        return saved;
    }

    /**
     * Get the latest credit score for a user by emailId.
     * First checks Redis cache; if not found, fetches from DB (cache-aside pattern).
     *
     * @param emailId the user's email
     * @return CreditScore entity
     */
    public CreditScore getCreditScoreByEmailId(String emailId) {
        // Try Redis cache first
        CreditScore creditScore = (CreditScore) redisTemplate.opsForValue().get(emailId);

        if (creditScore == null) {
            logger.info("Cache miss - Fetching from DB for: {}", emailId);
            creditScore = creditScoreRepository.findTopByEmailIdOrderByDateDesc(emailId);
        } else {
            logger.info("Cache hit - Fetched from Redis for: {}", emailId);
        }

        return creditScore;
    }

    /**
     * Get full score history for a user.
     */
    public List<CreditScore> getScoreHistory(Long userId) {
        logger.info("Fetching score history for userId: {}", userId);
        return creditScoreRepository.findByUserIdOrderByDateDesc(userId);
    }

    /**
     * Update an existing credit score for a user.
     */
    public CreditScore updateCreditScore(Long userId, CreditScoreDTO creditScoreDTO) {
        logger.info("Updating credit score for userId: {}", userId);
        CreditScore existing = creditScoreRepository.findTopByEmailIdOrderByDateDesc(creditScoreDTO.getEmailId());
        if (existing != null) {
            existing.setScore(creditScoreDTO.getScore());
            existing.setScoreType(creditScoreDTO.getScoreType());
            existing.setAlgorithmUsed(creditScoreDTO.getAlgorithmUsed());
            CreditScore updated = creditScoreRepository.save(existing);

            // Update Redis cache
            redisTemplate.opsForValue().set(creditScoreDTO.getEmailId(), updated);
            return updated;
        }
        return null;
    }

    /**
     * Delete credit score data for a user.
     */
    public void deleteCreditScore(Long userId) {
        logger.info("Deleting credit score for userId: {}", userId);
        creditScoreRepository.deleteByUserId(userId);
    }

    /**
     * Get average credit score across all users.
     */
    public double getAverageScore() {
        List<CreditScore> all = creditScoreRepository.findAll();
        return all.stream()
                .mapToInt(CreditScore::getScore)
                .average()
                .orElse(0.0);
    }

    /**
     * Save incoming financial data (submitted via POST from Data Collection layer).
     */
    public FinancialRecord saveFinancialData(FinancialDataDTO dto) {
        logger.info("Saving financial data for userId: {}", dto.getUserId());
        FinancialRecord record = new FinancialRecord();
        record.setUserId(dto.getUserId());
        record.setAmount(dto.getAmount());
        record.setTransactionType(dto.getTransactionType());
        record.setTransactionDescription(dto.getTransactionDescription());
        record.setCategory(dto.getCategory());
        return financialRecordRepository.save(record);
    }

    /**
     * Get all financial records for a user.
     */
    public List<FinancialRecord> getFinancialData(Long userId) {
        return financialRecordRepository.findByUserId(userId);
    }

    /**
     * Fetch user email from UserManagementMS via WebClient (inter-service communication).
     */
    public String getUserEmailFromUserMS(int userId) {
        return userManagementClient.getUserDetails(userId).block();
    }

    // Helper: convert DTO to entity
    private CreditScore convertToEntity(CreditScoreDTO dto) {
        CreditScore cs = new CreditScore();
        cs.setUserId(dto.getUserId());
        cs.setEmailId(dto.getEmailId());
        cs.setScore(dto.getScore());
        cs.setScoreType(dto.getScoreType());
        cs.setAlgorithmUsed(dto.getAlgorithmUsed());
        return cs;
    }
}
