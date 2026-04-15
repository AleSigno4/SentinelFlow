package com.example.sentinelflow.service;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.sentinelflow.model.Transaction;
import com.example.sentinelflow.repository.TransactionRepository;

@Service
public class TransactionGenerator {
    private final TransactionRepository transactionRepository;
    Random random = new Random();

    private final Map<String, List<String>> categories = Map.of(
        "FOOD", List.of("Starbucks", "McDonalds", "Supermarket", "Sushi Bar", "Pizza Delivery"),
        "SHOPPING", List.of("Amazon", "Zara", "Apple Store", "H&M", "eBay Purchase"),
        "ENTERTAINMENT", List.of("Netflix", "Steam", "Cinema", "Spotify", "PlayStation Store"),
        "UTILITIES", List.of("Electricity Bill", "Water Bill", "Internet Provider", "Gas Bill"),
        "TRAVEL", List.of("Uber", "Ryanair", "Train Ticket", "Gas Station", "Hotel Booking"),
        "CYBER", List.of("Unknown Proxy", "DarkWeb Entry", "Crypto Exchange", "VPN Service")
    );

    public TransactionGenerator(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void generateTransaction() {
        String category = "";
        String description = "";
        double amount = 0.0;
        Long userId = 0L;

        int chance = random.nextInt(100);

        if (chance < 5) {
            category = "CYBER";
            description = "DARKWEB ENTRY";
            amount = Math.round((random.nextDouble() * 10000) + 5000);
            userId = 99L;
            System.out.println("⚠️ ALERT: Generata transazione sospetta!");
        } else {
            category = categories.keySet().stream().skip(random.nextInt(categories.size())).findFirst().orElse("MISC");
            description = categories.get(category).get(random.nextInt(categories.get(category).size()));
            amount = Math.round((random.nextDouble() * 1000) + 1);
            userId = (long) (random.nextInt(10) + 1);
        }
        Transaction transaction = new Transaction(
            userId,
            java.math.BigDecimal.valueOf(amount),
            description,
            category,
            com.example.sentinelflow.model.TransactionStatus.PENDING,
            java.time.LocalDateTime.now()
        );

        this.transactionRepository.save(transaction);
    }
}
