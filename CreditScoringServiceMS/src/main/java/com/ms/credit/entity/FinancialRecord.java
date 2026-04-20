package com.ms.credit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "financial_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @Column(nullable = false)
    private Long userId;

    private LocalDate transactionDate;

    private BigDecimal amount;

    private String transactionType;        // credit / debit

    private String transactionDescription;

    private String category;               // utilities, groceries, entertainment, etc.

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDate.now();
    }
}
