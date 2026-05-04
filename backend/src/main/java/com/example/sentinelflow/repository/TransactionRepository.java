package com.example.sentinelflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.sentinelflow.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    @Query(value = "SELECT amount FROM transactions t WHERE t.user_id = :userId ORDER BY timestamp DESC LIMIT 10", nativeQuery = true)
    Double[] findLastAmountsByUserId(@Param("userId") Long userId);
}
