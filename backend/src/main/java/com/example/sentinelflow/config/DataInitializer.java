package com.example.sentinelflow.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.sentinelflow.model.Transaction;
import com.example.sentinelflow.model.TransactionStatus;
import com.example.sentinelflow.repository.TransactionRepository;

@Configuration
public class DataInitializer {
    private final TransactionRepository transactionRepository;

    public DataInitializer(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            Transaction t1 = new Transaction(1L, new BigDecimal("50.00"), "Grocery shopping", "Food", TransactionStatus.CONFIRMED, LocalDateTime.now().minusSeconds(5));
            Transaction t2 = new Transaction(2L, new BigDecimal("20.00"), "Movie ticket", "Entertainment", TransactionStatus.CONFIRMED, LocalDateTime.now().minusSeconds(10));
            Transaction t3 = new Transaction(3L, new BigDecimal("100.00"), "Electricity bill", "Utilities", TransactionStatus.REJECTED, LocalDateTime.now().minusSeconds(15));
            Transaction t4 = new Transaction(4L, new BigDecimal("150.00"), "New shoes", "Clothing", TransactionStatus.PENDING, LocalDateTime.now().minusSeconds(20));
            Transaction t5 = new Transaction(5L, new BigDecimal("100.00"), "Grocery shopping", "Food", TransactionStatus.CONFIRMED, LocalDateTime.now().minusSeconds(25));

            transactionRepository.saveAll(List.of(t1, t2, t3, t4, t5));

            System.out.println("Database popolato con successo!");
        };
    }
}
