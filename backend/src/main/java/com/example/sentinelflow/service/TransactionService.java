package com.example.sentinelflow.service;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.sentinelflow.model.Transaction;
import com.example.sentinelflow.model.TransactionStatus;
import com.example.sentinelflow.repository.TransactionRepository;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionAnalyzer transactionAnalyzer;
    Random random = new Random();

    private final Map<String, List<String>> categories = Map.of(
        "Food", List.of("Starbucks", "McDonalds", "Supermarket", "Sushi Bar", "Pizza Delivery", "Poke", "Patisserie", "Business Lunch", "Street Food Festival", "Coffee Roastery", "Wine Shop", "Gourmet Market", "Farmers Market", "Food Truck"),
        "Shopping", List.of("Amazon", "Zara", "Apple Store", "H&M", "eBay Purchase", "IKEA Furniture", "Nike Store", "Pharmacy", "Sephora", "Bookstore", "Pet Shop Supplies", "Gadget Store", "Clothing Boutique", "Home Decor", "Toy Store"),
        "Entertainment", List.of("Netflix", "Steam", "Cinema", "Spotify", "PlayStation Store", "Disney+", "Audible Subscription", "Concert Ticket", "Museum Entry", "Gym Membership", "Bowling Alley", "Tennis Court Rental", "Golf Course Fee", "Amusement Park", "Escape Room"),
        "Utilities", List.of("Electricity Bill", "Water Bill", "Internet Provider", "Gas Bill", "Mobile Phone Plan", "Cloud Storage (iCloud/Drive)", "Waste Tax", "Home Insurance", "Software License", "LinkedIn Premium", "Online Course Subscription", "VPN Service"),
        "Travel", List.of("Uber", "Ryanair", "Train Ticket", "Gas Station", "Hotel Booking", "Parking Fee", "Electric Scooter Rental", "Highway Toll", "Airport Duty Free", "Public Transport Pass", "Car Wash", "Bike Rental", "Car Rental", "Travel Insurance", "Luggage Storage"),
        "Cyber", List.of("DarkWeb Entry", "Crypto Exchange", "VPN Service"),
        "Health", List.of("Pharmacy", "Doctor Visit", "Gym Membership", "Yoga Class", "Health Insurance", "Vitamin Store", "Therapy Session", "Dental Checkup", "Optician Visit", "Massage Therapy", "Personal Trainer Session")
    );

    private record Range(double min, double max) {}

    private final Map<String, Range> categoryRanges = Map.of(
        "Food", new Range(5.0, 150.0),
        "Shopping", new Range(20.0, 700.0),
        "Entertainment", new Range(3.0, 300.0),
        "Utilities", new Range(30.0, 500.0),
        "Travel", new Range(50.0, 2000.0),
        "Cyber", new Range(1000.0, 10000.0),
        "Health", new Range(20.0, 200.0)
    );

    public TransactionService(TransactionRepository transactionRepository, TransactionAnalyzer transactionAnalyzer) {
        this.transactionRepository = transactionRepository;
        this.transactionAnalyzer = transactionAnalyzer;
    }

    @Scheduled(fixedRate = 5000)
    public void generateTransaction() {
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
            amount = Math.round((random.nextDouble() * (categoryRanges.get(category).max() - categoryRanges.get(category).min())) + categoryRanges.get(category).min());
            userId = (long) (random.nextInt(10) + 1);
        }
        Transaction transaction = new Transaction(
            userId,
            amount,
            description,
            category,
            com.example.sentinelflow.model.TransactionStatus.PENDING,
            java.time.LocalDateTime.now(),
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