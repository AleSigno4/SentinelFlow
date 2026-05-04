package com.example.sentinelflow.service;

import java.util.AbstractMap;
import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.example.sentinelflow.model.Transaction;
import com.example.sentinelflow.repository.TransactionRepository;

@Service
public class TransactionAnalyzer {

    private final TransactionRepository transactionRepository;

    public TransactionAnalyzer(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public AbstractMap.SimpleEntry<Double, String> calculateRiskScore(Transaction transaction) {
        double riskScore = 0.0;
        StringBuilder aiReason = new StringBuilder();

        Double[] lastAmounts = transactionRepository.findLastAmountsByUserId(transaction.getUserId());
        
        if (lastAmounts != null && lastAmounts.length > 0) {
            double avg = Arrays.stream(lastAmounts).mapToDouble(Double::doubleValue).average().orElse(0.0);
            
            if (transaction.getAmount() > avg * 3) {
                riskScore += 0.5;
                aiReason.append("Anomaly amount; ");
            } else if (transaction.getAmount() > avg * 2) {
                riskScore += 0.25;
                aiReason.append("High amount; ");
            }
        } else {
            if (transaction.getAmount() > 1000.0) {
                riskScore += 0.2;
                aiReason.append("New user with significant amount; ");
            }
        }

        if ("Cyber".equalsIgnoreCase(transaction.getCategory())) {
            riskScore += 0.7;
            aiReason.append("High-risk category (Cyber); ");
        }

        int hour = transaction.getTimestamp().getHour();
        if (hour < 6 || hour > 22) {
            riskScore += 0.15;
            aiReason.append("Unusual nighttime hour; ");
        }

        double finalScore = Math.min(riskScore, 1.0);
        
        String finalReason = aiReason.toString().isEmpty() 
            ? "Standard transaction" 
            : aiReason.toString().trim().replaceAll(";$", "");

        return new AbstractMap.SimpleEntry<>(finalScore, finalReason);
    }
}
