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

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
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
            description = "Darkweb Entry";
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

    public Transaction updateStatus(Long id, TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transaction.setStatus(status);
        return transactionRepository.save(transaction);
    }
}