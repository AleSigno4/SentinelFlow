package com.example.sentinelflow.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sentinelflow.model.Transaction;


public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    @Query(value = "SELECT amount FROM transactions t WHERE t.user_id = :userId ORDER BY timestamp DESC LIMIT 10", nativeQuery = true)
    Double[] findLastAmountsByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT timestamp FROM transactions WHERE user_id = :userId ORDER BY timestamp DESC LIMIT 5", nativeQuery = true)
    LocalDateTime[] findLastTimestampsByUserId(@Param("userId") Long userId);
}
