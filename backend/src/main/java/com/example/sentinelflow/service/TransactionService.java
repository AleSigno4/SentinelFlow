package com.example.sentinelflow.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.sentinelflow.config.TransactionRules;
import com.example.sentinelflow.model.Transaction;
import com.example.sentinelflow.model.TransactionStatus;
import com.example.sentinelflow.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionAnalyzer transactionAnalyzer;
    private final Map<String, List<String>> categories = TransactionRules.CATEGORIES;
    private final Map<String, TransactionRules.Range> categoryRanges = TransactionRules.CATEGORYRANGES;
    private final Map<String, Double> fixedPrices = TransactionRules.FIXEDPRICES;

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    Random random = new Random();

    public TransactionService(TransactionRepository transactionRepository, TransactionAnalyzer transactionAnalyzer) {
        this.transactionRepository = transactionRepository;
        this.transactionAnalyzer = transactionAnalyzer;
    }

    @Scheduled(fixedRate = 10000, initialDelay = 2000)
    public void generateTransaction() {
        generateTransaction(java.time.LocalDateTime.now());
    }

    public void generateTransaction(java.time.LocalDateTime timestamp) {
        int chance = random.nextInt(100);

        switch (chance) {
            case 0, 1, 2 -> {
                generateCyberTransaction(timestamp);
            }
            case 3, 4 -> {
                generateAnomalousDescriptionTransaction(timestamp);
            }
            case 5, 6 -> {
                generateAnomalousSubscriptionTransaction(timestamp);
            }
            case 7, 8 -> {
                generateBurstTransactions(timestamp);
            }
            case 9, 10 -> {
                generateAnomalousAmountTransaction(timestamp);
            }
            default -> {
                generateNormalTransaction(timestamp);
            }
        }

    }

    private void generateCyberTransaction(LocalDateTime timestamp) {
        String category = "Cyber";
        String description = categories.get(category).get(random.nextInt(categories.get(category).size()));
        double amount = Math.round((random.nextDouble() * (categoryRanges.get(category).max() - categoryRanges.get(category).min())) + categoryRanges.get(category).min());
        Long userId = (long) (random.nextInt(100) + 1);
        logger.warn("⚠️ ALERT: Suspicious transaction generated!");
        saveTransaction(userId, amount, description, category, timestamp);
    }

    private void generateAnomalousDescriptionTransaction(LocalDateTime timestamp) {
        String category = categories.keySet().stream().skip(random.nextInt(categories.size())).findFirst().orElse("MISC");
        String description = categories.get("Cyber").get(random.nextInt(categories.get("Cyber").size()));
        double amount = Math.round((random.nextDouble() * (categoryRanges.get(category).max() - categoryRanges.get(category).min())) + categoryRanges.get(category).min());
        Long userId = (long) (random.nextInt(100) + 1);
        logger.warn("⚠️ ALERT: Anomalous description transaction generated!");
        saveTransaction(userId, amount, description, category, timestamp);
    }

    private void generateAnomalousSubscriptionTransaction(LocalDateTime timestamp) {
        String category = "Subscriptions";
        String description = categories.get(category).get(random.nextInt(categories.get(category).size()));
        double amount = Math.round((random.nextDouble() * (categoryRanges.get(category).max() - categoryRanges.get(category).min())) + categoryRanges.get(category).min());
        Long userId = (long) (random.nextInt(100) + 1);
        logger.warn("⚠️ ALERT: Anomalous subscription transaction generated!");
        saveTransaction(userId, amount, description, category, timestamp);
    }

    private void generateBurstTransactions(LocalDateTime timestamp) {
        Long userId = (long) (random.nextInt(100) + 1);

        for (int i = 0; i < 5; i++) {
            String category = categories.keySet().stream().skip(random.nextInt(categories.size())).findFirst().orElse("MISC");
            String description = categories.get(category).get(random.nextInt(categories.get(category).size()));
            double amount = Math.round((random.nextDouble() * (categoryRanges.get(category).max() - categoryRanges.get(category).min())) + categoryRanges.get(category).min());

            saveTransaction(userId, amount, description, category, timestamp.plusSeconds(i * 2));
        }

        logger.warn("⚠️ ALERT: Generated burst of transactions!");
    }

    private void generateAnomalousAmountTransaction(LocalDateTime timestamp) {
        String category = categories.keySet().stream().skip(random.nextInt(categories.size())).findFirst().orElse("MISC");
        String description = categories.get(category).get(random.nextInt(categories.get(category).size()));
        double amount = Math.round((random.nextDouble() * (categoryRanges.get(category).max() - categoryRanges.get(category).min())) + categoryRanges.get(category).min()) * (random.nextInt(5) + 1);
        Long userId = (long) (random.nextInt(100) + 1);
        logger.warn("⚠️ ALERT: Generated transaction with anomalous amount!");
        saveTransaction(userId, amount, description, category, timestamp);
    }

    private void generateNormalTransaction(LocalDateTime timestamp) {
        String category = categories.keySet().stream().skip(random.nextInt(categories.size())).findFirst().orElse("MISC");
        String description = categories.get(category).get(random.nextInt(categories.get(category).size()));
        double amount;
        if (fixedPrices.containsKey(description)) {
            amount = fixedPrices.get(description);
        } else {
            amount = Math.round((random.nextDouble() * (categoryRanges.get(category).max() - categoryRanges.get(category).min())) + categoryRanges.get(category).min());
        }
        Long userId = (long) (random.nextInt(100) + 1);
        logger.info("✅ Generating normal transaction...");
        saveTransaction(userId, amount, description, category, timestamp);
    }

    private void saveTransaction(Long userId, double amount, String description, String category, LocalDateTime timestamp) {
        Transaction transaction = new Transaction(
                userId,
                amount,
                description,
                category,
                TransactionStatus.PENDING,
                timestamp,
                0.0,
                "N/A"
        );

        var aiResult = transactionAnalyzer.calculateRiskScore(transaction);
        transaction.setRiskScore(aiResult.getKey());
        transaction.setAiReason(aiResult.getValue());

        boolean isFraudByAI = transactionAnalyzer.isFraudulentByAI(transaction);
        if (isFraudByAI) {
            transaction.setStatus(TransactionStatus.REJECTED);
            transaction.setAiReason(transaction.getAiReason() + " [AI BLOCK]");
            logger.info("🤖 AI detected fraud! Status set to REJECTED.");
        } else {
            transaction.setStatus(TransactionStatus.CONFIRMED);
        }

        this.transactionRepository.save(transaction);
    }

    public Transaction updateStatus(Long id, TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("Trying to update non-existent transaction: ID={}", id);
                throw new RuntimeException("Transaction not found");
            });
        transaction.setStatus(status);
        transaction.setManualOverride(true);
        transaction.setManualOverrideTimestamp(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }
}
