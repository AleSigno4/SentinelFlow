package com.example.sentinelflow.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.Arrays;

import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.sentinelflow.config.TransactionRules;
import com.example.sentinelflow.dto.FraudDetectionRequest;
import com.example.sentinelflow.dto.FraudDetectionResponse;
import com.example.sentinelflow.model.Transaction;
import com.example.sentinelflow.repository.TransactionRepository;

@Service
public class TransactionAnalyzer {

    private final TransactionRepository transactionRepository;
    private final RestClient aiRestClient;

    public TransactionAnalyzer(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        this.aiRestClient = RestClient.builder()
                .baseUrl("http://localhost:8000")
                .requestFactory(new SimpleClientHttpRequestFactory())
                .build();
    }

    public boolean isFraudulentByAI(Transaction transaction) {
        try {
            LocalDateTime[] lastTimestamps = transactionRepository.findLastTimestampsByUserId(transaction.getUserId());

            int txCount3min = 1;
            double timeDiff = 0.0;

            if (lastTimestamps != null && lastTimestamps.length > 0) {
                LocalDateTime mostRecent = lastTimestamps[0];
                timeDiff = ChronoUnit.SECONDS.between(mostRecent, transaction.getTimestamp()) / 60.0;

                LocalDateTime windowStart = transaction.getTimestamp().minusMinutes(3);
                for (LocalDateTime ts : lastTimestamps) {
                    if (ts.isAfter(windowStart)) {
                        txCount3min++;
                    }
                }
            }

            int categoryId = mapCategoryToInt(transaction.getCategory());

            FraudDetectionRequest requestData = new FraudDetectionRequest(
                    transaction.getAmount(),
                    categoryId,
                    transaction.getDescription(),
                    transaction.getTimestamp().getHour(),
                    txCount3min,
                    timeDiff
            );
            
            FraudDetectionResponse response = aiRestClient.post()
                    .uri("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Connection", "close")
                    .body(requestData)
                    .retrieve()
                    .body(FraudDetectionResponse.class);

            return response != null && response.isFraud();

        } catch (Exception e) {
            System.err.println("🚨 Errore durante la chiamata a FastAPI: " + e.getMessage());
            return false;
        }
    }

    private int mapCategoryToInt(String category) {
        if (category == null) {
            return 11;
        }

        return switch (category.trim()) {
            case "Beauty" ->
                0;
            case "Clothes" ->
                1;
            case "Cyber" ->
                2;
            case "Entertainment" ->
                3;
            case "Food" ->
                4;
            case "Health" ->
                5;
            case "Insurance" ->
                6;
            case "Shopping" ->
                7;
            case "Subscriptions" ->
                8;
            case "Transport" ->
                9;
            case "Travel" ->
                10;
            case "Utilities" ->
                11;
            default ->
                11;
        };
    }

    public AbstractMap.SimpleEntry<Double, String> calculateRiskScore(Transaction transaction) {
        double riskScore = 0.0;
        StringBuilder aiReason = new StringBuilder();

        Double[] lastAmounts = transactionRepository.findLastAmountsByUserId(transaction.getUserId());
        LocalDateTime[] lastTimestamps = transactionRepository.findLastTimestampsByUserId(transaction.getUserId());

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

        if (lastTimestamps != null && lastTimestamps.length >= 4) {
            LocalDateTime oldestInWindow = lastTimestamps[lastTimestamps.length - 1];

            if (oldestInWindow.isAfter(transaction.getTimestamp().minusMinutes(3))) {
                riskScore += 0.6;
                aiReason.append("High velocity (5 trans. in < 3min); ");
            }
        }

        if ("Subscriptions".equals(transaction.getCategory())) {
            Double expectedPrice = TransactionRules.FIXEDPRICES.get(transaction.getDescription());

            if (expectedPrice != null && !expectedPrice.equals(transaction.getAmount())) {
                riskScore += 0.4;
                aiReason.append("Subscription price mismatch (expected ").append(expectedPrice).append("); ");
            }
        }

        if ("Cyber".equalsIgnoreCase(transaction.getCategory())) {
            riskScore += 0.7;
            aiReason.append("High-risk category (Cyber); ");
        }

        boolean hasBlacklistedKey = TransactionRules.BLACKLISTED_KEYWORDS.stream()
                .anyMatch(key -> transaction.getDescription().toLowerCase().contains(key.toLowerCase()));

        boolean isEducationalContext = TransactionRules.EDUCATIONAL_KEYWORDS.stream()
                .anyMatch(key -> transaction.getDescription().toLowerCase().contains(key.toLowerCase()));

        if (hasBlacklistedKey && !isEducationalContext && !"Cyber".equalsIgnoreCase(transaction.getCategory())) {
            riskScore += 0.7;
            aiReason.append("Suspicious keyword in non-cyber category; ");
        }

        int hour = transaction.getTimestamp().getHour();
        if (hour < 7 || hour > 21) {
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
