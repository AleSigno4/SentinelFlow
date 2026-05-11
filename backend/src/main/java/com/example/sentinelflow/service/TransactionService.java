package com.example.sentinelflow.service;

import java.util.List;
import java.util.Map;
import java.util.Random;

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

    Random random = new Random();

    public TransactionService(TransactionRepository transactionRepository, TransactionAnalyzer transactionAnalyzer) {
        this.transactionRepository = transactionRepository;
        this.transactionAnalyzer = transactionAnalyzer;
    }

    @Scheduled(fixedRate = 5000, initialDelay = 2000)
    public void generateTransaction() {
        generateTransaction(java.time.LocalDateTime.now());
    }

    public void generateTransaction(java.time.LocalDateTime timestamp) {
        String category;
        String description;
        double amount;
        Long userId;

        int chance = random.nextInt(100);

        if (chance < 5) {
            category = "Cyber";
            description = categories.get(category).get(random.nextInt(categories.get(category).size()));
            amount = Math.round((random.nextDouble() * (categoryRanges.get(category).max() - categoryRanges.get(category).min())) + categoryRanges.get(category).min());
            userId = 99L;
            System.out.println("⚠️ ALERT: Generata transazione sospetta!");
        } else {
            category = categories.keySet().stream().skip(random.nextInt(categories.size())).findFirst().orElse("MISC");
            description = categories.get(category).get(random.nextInt(categories.get(category).size()));
            if (fixedPrices.containsKey(description)) {
                amount = fixedPrices.get(description);
            } else {
                amount = Math.round((random.nextDouble() * (categoryRanges.get(category).max() - categoryRanges.get(category).min())) + categoryRanges.get(category).min());
            }
            userId = (long) (random.nextInt(100) + 1);
        }
        Transaction transaction = new Transaction(
                userId,
                amount,
                description,
                category,
                com.example.sentinelflow.model.TransactionStatus.PENDING,
                timestamp,
                0.0,
                "N/A"
        );

        var aiResult = transactionAnalyzer.calculateRiskScore(transaction);
        transaction.setRiskScore(aiResult.getKey());
        transaction.setAiReason(aiResult.getValue());

        this.transactionRepository.save(transaction);
    }

    public Transaction updateStatus(Long id, TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transaction.setStatus(status);
        return transactionRepository.save(transaction);
    }
}
