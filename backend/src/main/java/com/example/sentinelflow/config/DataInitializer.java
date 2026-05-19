package com.example.sentinelflow.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.sentinelflow.repository.TransactionRepository;
import com.example.sentinelflow.service.TransactionService;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(
            TransactionRepository repository,
            TransactionService transactionService) {
        return args -> {
            if (repository.count() == 0) {
                System.out.println("Empty DB, Generate 100 transactions...");

                int totalTransactions = 500;
                int intervalMinutes = 20;
                for (int i = 0; i < totalTransactions; i++) {
                    int minutesToSubtract = (totalTransactions - i) * intervalMinutes;
                    java.time.LocalDateTime pastDate = java.time.LocalDateTime.now().minusMinutes(minutesToSubtract);
                    transactionService.generateTransaction(pastDate);
                }

                System.out.println("✅ Database correctly initialized with 500 transactions.");
            } else {
                System.out.println("ℹ️ Database already contains data, skipping initialization.");
            }
        };
    }
}
