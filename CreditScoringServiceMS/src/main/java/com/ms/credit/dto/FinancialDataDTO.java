package com.ms.credit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialDataDTO {
    private Long userId;
    private String emailId;
    private BigDecimal amount;
    private String transactionType;         // credit / debit
    private String transactionDescription;
    private String category;
    private int creditScore;               // Kafka will carry this to trigger email
}
