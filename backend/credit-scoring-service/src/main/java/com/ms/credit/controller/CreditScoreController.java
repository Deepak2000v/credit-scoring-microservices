package com.ms.credit.controller;

import com.ms.credit.dto.CreditScoreDTO;
import com.ms.credit.dto.FinancialDataDTO;
import com.ms.credit.entity.CreditScore;
import com.ms.credit.entity.FinancialRecord;
import com.ms.credit.service.CreditScoreService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CreditScoreController {

    private static final Logger logger = LogManager.getLogger(CreditScoreController.class);

    @Autowired
    private CreditScoreService creditScoreService;

    // =============================================
    // CREDIT SCORE APIs
    // =============================================

    /**
     * POST /score/calculate
     * Calculate and save a new credit score.
     * Triggers Redis cache + Kafka email notification.
     * Body: { "userId": 1, "emailId": "user@example.com", "score": 720,
     *         "scoreType": "FICO", "algorithmUsed": "StandardAlgo" }
     */
    @PostMapping("/score/calculate")
    public ResponseEntity<?> calculateScore(@RequestBody CreditScoreDTO creditScoreDTO) {
        logger.info("POST /score/calculate called for userId: {}", creditScoreDTO.getUserId());
        CreditScore result = creditScoreService.calculateCreditScore(creditScoreDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /score/{userId}
     * Fetch the latest credit score for a user by emailId query param.
     * Checks Redis cache first, then DB.
     * Example: GET /score/1?emailId=user@example.com
     */
    @GetMapping("/score/{userId}")
    public ResponseEntity<?> getScore(@PathVariable Long userId,
                                      @RequestParam String emailId) {
        logger.info("GET /score/{} called", userId);
        CreditScore score = creditScoreService.getCreditScoreByEmailId(emailId);
        if (score != null) {
            return ResponseEntity.ok(score);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * GET /score/history/{userId}
     * Retrieve full score history for a user.
     */
    @GetMapping("/score/history/{userId}")
    public ResponseEntity<?> getScoreHistory(@PathVariable Long userId) {
        logger.info("GET /score/history/{} called", userId);
        List<CreditScore> history = creditScoreService.getScoreHistory(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * PUT /score/{userId}
     * Update the credit score for a user.
     */
    @PutMapping("/score/{userId}")
    public ResponseEntity<?> updateScore(@PathVariable Long userId,
                                         @RequestBody CreditScoreDTO creditScoreDTO) {
        logger.info("PUT /score/{} called", userId);
        CreditScore updated = creditScoreService.updateCreditScore(userId, creditScoreDTO);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * DELETE /score/{userId}
     * Delete credit score data for a user.
     */
    @DeleteMapping("/score/{userId}")
    public ResponseEntity<?> deleteScore(@PathVariable Long userId) {
        logger.info("DELETE /score/{} called", userId);
        creditScoreService.deleteCreditScore(userId);
        return ResponseEntity.ok(Map.of("message", "Credit score deleted for userId: " + userId));
    }

    /**
     * GET /score/average
     * Get the average credit score across all users.
     */
    @GetMapping("/score/average")
    public ResponseEntity<?> getAverageScore() {
        logger.info("GET /score/average called");
        double avg = creditScoreService.getAverageScore();
        return ResponseEntity.ok(Map.of("averageScore", avg));
    }

    /**
     * PUT /score/refresh
     * Placeholder for refreshing/recalculating all scores based on latest data.
     */
    @PutMapping("/score/refresh")
    public ResponseEntity<?> refreshScores() {
        logger.info("PUT /score/refresh called");
        return ResponseEntity.ok(Map.of("message", "Score refresh triggered successfully"));
    }

    // =============================================
    // FINANCIAL DATA APIs
    // (replaces external Data Collection Service)
    // =============================================

    /**
     * POST /data
     * Submit new financial data for a user (replaces external API call).
     * Body: { "userId": 1, "emailId": "user@example.com", "amount": 5000.00,
     *         "transactionType": "credit", "transactionDescription": "Salary",
     *         "category": "income" }
     */
    @PostMapping("/data")
    public ResponseEntity<?> submitFinancialData(@RequestBody FinancialDataDTO financialDataDTO) {
        logger.info("POST /data called for userId: {}", financialDataDTO.getUserId());
        FinancialRecord record = creditScoreService.saveFinancialData(financialDataDTO);
        return ResponseEntity.ok(record);
    }

    /**
     * GET /data/{userId}
     * Retrieve all financial records for a user.
     */
    @GetMapping("/data/{userId}")
    public ResponseEntity<?> getFinancialData(@PathVariable Long userId) {
        logger.info("GET /data/{} called", userId);
        List<FinancialRecord> records = creditScoreService.getFinancialData(userId);
        return ResponseEntity.ok(records);
    }

    // =============================================
    // INTER-SERVICE COMMUNICATION
    // =============================================

    /**
     * GET /credit/user/{userId}
     * Demo endpoint: fetches user email from UserManagementMS
     * and returns it. Shows inter-service communication via WebClient.
     */
    @GetMapping("/credit/user/{userId}")
    public ResponseEntity<?> getUserFromUserMS(@PathVariable int userId) {
        logger.info("GET /credit/user/{} - calling UserManagementMS", userId);
        String email = creditScoreService.getUserEmailFromUserMS(userId);
        if (email != null) {
            return ResponseEntity.ok(Map.of("userId", userId, "email", email));
        }
        return ResponseEntity.notFound().build();
    }
}
