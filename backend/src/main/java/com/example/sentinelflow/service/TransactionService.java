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

    @Scheduled(fixedRate = 10000, initialDelay = 2000)
    public void generateTransaction() {
        generateTransaction(java.time.LocalDateTime.now());
    }

    public void generateTransaction(java.time.LocalDateTime timestamp) {
        String category;
        String description;
        double amount;
        Long userId;

        int chance = random.nextInt(100);

        switch(chance) {
            case 0, 1, 2 -> {
                category = "Cyber";
                description = categories.get(category).get(random.nextInt(categories.get(category).size()));
                amount = Math.round((random.nextDouble() * (categoryRanges.get(category).max() - categoryRanges.get(category).min())) + categoryRanges.get(category).min());
                userId = (long) (random.nextInt(100) + 1);
                System.out.println("⚠️ ALERT: Generata transazione sospetta!");
            }
            case 3, 4 -> {
                category = categories.keySet().stream().skip(random.nextInt(categories.size())).findFirst().orElse("MISC");
                description = categories.get("Cyber").get(random.nextInt(categories.get("Cyber").size()));
                amount = Math.round((random.nextDouble() * (categoryRanges.get(category).max() - categoryRanges.get(category).min())) + categoryRanges.get(category).min());
                userId = (long) (random.nextInt(100) + 1);
                System.out.println("⚠️ ALERT: Generata transazione con descrizione anomala!");
            }
            case 5, 6 -> {
                category = "Subscriptions";
                description = categories.get(category).get(random.nextInt(categories.get(category).size()));
                amount = Math.round((random.nextDouble() * (categoryRanges.get(category).max() - categoryRanges.get(category).min())) + categoryRanges.get(category).min());
                userId = (long) (random.nextInt(100) + 1);
                System.out.println("⚠️ ALERT: Generata transazione di abbonamento con prezzo anomalo!");
            }
            case 7, 8 -> {
                userId = (long) (random.nextInt(100) + 1);
                for(int i = 0; i < 5; i++) {
                    category = categories.keySet().stream().skip(random.nextInt(categories.size())).findFirst().orElse("MISC");
                    description = categories.get(category).get(random.nextInt(categories.get(category).size()));
                    amount = Math.round((random.nextDouble() * (categoryRanges.get(category).max() - categoryRanges.get(category).min())) + categoryRanges.get(category).min());
                    Transaction transaction = new Transaction(
                            userId,
                            amount,
                            description,
                            category,
                            TransactionStatus.PENDING,
                            timestamp.plusSeconds(i * 2),
                            0.0,
                            "N/A"
                    );
                    
                    // Calcolo delle regole storiche
                    var aiResult = transactionAnalyzer.calculateRiskScore(transaction);
                    transaction.setRiskScore(aiResult.getKey());
                    transaction.setAiReason(aiResult.getValue());

                    // --- INTEGRAZIONE IA PER IL BURST ---
                    boolean isFraud = transactionAnalyzer.isFraudulentByAI(transaction);
                    if (isFraud) {
                        transaction.setStatus(TransactionStatus.REJECTED);
                        transaction.setAiReason(transaction.getAiReason() + " [AI BLOCK]");
                    } else {
                        transaction.setStatus(TransactionStatus.CONFIRMED);
                    }

                    this.transactionRepository.save(transaction);
                }
                return;
            }
            case 9, 10 -> {
                category = categories.keySet().stream().skip(random.nextInt(categories.size())).findFirst().orElse("MISC");
                description = categories.get(category).get(random.nextInt(categories.get(category).size()));
                amount = Math.round((random.nextDouble() * (categoryRanges.get(category).max() - categoryRanges.get(category).min())) + categoryRanges.get(category).min()) * (random.nextInt(5) + 1);
                userId = (long) (random.nextInt(100) + 1);
                System.out.println("⚠️ ALERT: Generata transazione con importo anomalo!");
            }
            default -> {
                category = categories.keySet().stream().skip(random.nextInt(categories.size())).findFirst().orElse("MISC");
                description = categories.get(category).get(random.nextInt(categories.get(category).size()));
                if (fixedPrices.containsKey(description)) {
                    amount = fixedPrices.get(description);
                } else {
                    amount = Math.round((random.nextDouble() * (categoryRanges.get(category).max() - categoryRanges.get(category).min())) + categoryRanges.get(category).min());
                }
                userId = (long) (random.nextInt(100) + 1);
            }
        }
        
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

        // Calcolo vecchio basato sulle regole hardcoded
        var aiResult = transactionAnalyzer.calculateRiskScore(transaction);
        transaction.setRiskScore(aiResult.getKey());
        transaction.setAiReason(aiResult.getValue());

        // --- INTEGRAZIONE CHIAMATA FASTAPI (IA) ---
        boolean isFraudByAI = transactionAnalyzer.isFraudulentByAI(transaction);
        if (isFraudByAI) {
            transaction.setStatus(TransactionStatus.REJECTED);
            transaction.setAiReason(transaction.getAiReason() + " [AI BLOCK]");
            System.out.println("🤖 L'IA ha intercettato una frode! Stato impostato su REJECTED.");
        } else {
            transaction.setStatus(TransactionStatus.CONFIRMED);
        }

        this.transactionRepository.save(transaction);
    }

    public Transaction updateStatus(Long id, TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transaction.setStatus(status);
        return transactionRepository.save(transaction);
    }
}