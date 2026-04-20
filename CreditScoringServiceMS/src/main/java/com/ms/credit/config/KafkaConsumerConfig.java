package com.ms.credit.config;

import com.ms.credit.dto.FinancialDataDTO;
import com.ms.credit.service.EmailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerConfig {

    private static final Logger logger = LogManager.getLogger(KafkaConsumerConfig.class);

    @Autowired
    private EmailService emailService;

    /**
     * Listens to the "credit-score-updates" Kafka topic.
     * When a new credit score is calculated, it triggers an email notification to the user.
     *
     * @param message FinancialDataDTO containing emailId and creditScore from the producer
     */
    @KafkaListener(topics = "credit-score-updates", groupId = "group_id")
    public void handleClaimStatusUpdate(FinancialDataDTO message) {
        logger.info("Kafka message received for user: {}", message.getEmailId());

        // Send email notification to the user about their updated credit score
        emailService.sendEmail(
                message.getEmailId(),
                "Credit Score Update",
                "Your Credit Score has been updated to: " + message.getCreditScore()
        );
    }
}
