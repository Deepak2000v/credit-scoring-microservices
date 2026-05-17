package com.ms.credit.repository;

import com.ms.credit.entity.CreditScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditScoreRepository extends JpaRepository<CreditScore, Long> {

    // Find the most recent credit score for a user, ordered by date descending
    CreditScore findTopByEmailIdOrderByDateDesc(String emailId);

    // Get full score history for a user
    List<CreditScore> findByUserIdOrderByDateDesc(Long userId);

    // Delete all scores for a user
    void deleteByUserId(Long userId);
}
