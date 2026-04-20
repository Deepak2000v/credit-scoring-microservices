package com.ms.credit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "credit_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long creditScoreId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String emailId;

    private int score;

    private LocalDate date;

    private String scoreType;       // e.g. FICO, VantageScore

    @Column(columnDefinition = "TEXT")
    private String scoreHistory;    // JSON string tracking historical scores

    private String algorithmUsed;   // Details about the scoring algorithm

    @PrePersist
    protected void onCreate() {
        date = LocalDate.now();
    }
}
