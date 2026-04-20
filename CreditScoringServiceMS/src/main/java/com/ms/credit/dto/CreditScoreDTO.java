package com.ms.credit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditScoreDTO {
    private Long userId;
    private String emailId;
    private int score;
    private String scoreType;       // e.g. FICO, VantageScore
    private String algorithmUsed;
}
