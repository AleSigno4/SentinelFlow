package com.example.sentinelflow.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.sentinelflow.model.Transaction;
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
            Transaction t1 = new Transaction(new BigDecimal("50.00"), "Grocery shopping", "Food", LocalDateTime.now().minusDays(1));
            Transaction t2 = new Transaction(new BigDecimal("20.00"), "Movie ticket", "Entertainment", LocalDateTime.now().minusDays(2));
            Transaction t3 = new Transaction(new BigDecimal("100.00"), "Electricity bill", "Utilities", LocalDateTime.now().minusDays(3));
            Transaction t4 = new Transaction(new BigDecimal("150.00"), "New shoes", "Clothing", LocalDateTime.now().minusDays(4));
            Transaction t5 = new Transaction(new BigDecimal("100.00"), "Grocery shopping", "Food", LocalDateTime.now().minusDays(4));

            transactionRepository.saveAll(List.of(t1, t2, t3, t4, t5));

            System.out.println("Database popolato con successo!");
        };
    }
}
