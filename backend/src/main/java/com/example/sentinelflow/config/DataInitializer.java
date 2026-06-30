package com.example.sentinelflow.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.sentinelflow.repository.TransactionRepository;
import com.example.sentinelflow.service.TransactionService;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initDatabase(
            TransactionRepository repository,
            TransactionService transactionService) {
        return args -> {
            if (repository.count() == 0) {
                logger.info("Empty DB, Generate 500 transactions...");

                int totalTransactions = 500;
                int intervalMinutes = 20;
                for (int i = 0; i < totalTransactions; i++) {
                    int minutesToSubtract = (totalTransactions - i) * intervalMinutes;
                    java.time.LocalDateTime pastDate = java.time.LocalDateTime.now().minusMinutes(minutesToSubtract);
                    transactionService.generateTransaction(pastDate);
                }

                logger.info("✅ Database correctly initialized with 500 transactions.");
            } else {
                logger.info("ℹ️ Database already contains data, skipping initialization.");
            }
        };
    }
}
