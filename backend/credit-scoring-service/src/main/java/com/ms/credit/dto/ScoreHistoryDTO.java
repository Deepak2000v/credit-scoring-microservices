package com.ms.credit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreHistoryDTO {
    private Long userId;
    private int score;
    private LocalDate date;
    private String scoreType;
}
