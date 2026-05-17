package com.ms.credit.repository;

import com.ms.credit.entity.FinancialRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {
    List<FinancialRecord> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
