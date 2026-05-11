package com.example.sentinelflow.config;

import java.util.List;
import java.util.Map;

public class TransactionRules {

    public static final Map<String, List<String>> CATEGORIES = Map.ofEntries(
            Map.entry("Food", List.of("Starbucks", "McDonalds", "Supermarket", "Sushi Bar", "Pizza Delivery", "Poke", "Patisserie", "Business Lunch", "Street Food Festival", "Coffee Roastery", "Wine Shop", "Gourmet Market", "Farmers Market", "Food Truck")),
            Map.entry("Shopping", List.of("Amazon", "Apple Store", "eBay Purchase", "IKEA Furniture", "Bookstore", "Pet Shop Supplies", "Home Decor", "Toy Store", "Electronics Store", "Office Supplies")),
            Map.entry("Clothes", List.of("Zara", "H&M", "Hollister", "Pull&Bear", "Nike Store", "Clothing Boutique", "Footwear Store", "Luxury Fashion", "Sportswear Shop", "Children's Clothing")),
            Map.entry("Beauty", List.of("Amazon", "Sephora", "Lush Cosmetics", "Hair Salon", "Nail Salon", "Spa Treatment", "Makeup Artist", "Skincare Clinic", "Fragrance Store", "Barber Shop")),
            Map.entry("Entertainment", List.of("Steam", "Cinema", "PlayStation Store", "Concert Ticket", "Museum Entry", "Gym Membership", "Bowling Alley", "Tennis Court Rental", "Golf Course Fee", "Amusement Park", "Escape Room")),
            Map.entry("Subscriptions", List.of("Netflix", "Spotify", "Disney+", "LinkedIn Premium", "ChatGPT Plus", "Amazon Prime", "HBO Max", "Apple Music", "Audible Subscription", "Online Newspaper")),
            Map.entry("Utilities", List.of("Electricity Bill", "Water Bill", "Internet Provider", "Gas Bill", "Mobile Phone Plan", "Cloud Storage (iCloud/Drive)", "Waste Tax", "Home Insurance", "Software License", "Online Course Subscription", "VPN Service")),
            Map.entry("Travel", List.of("Ryanair", "Hotel Booking", "Car Rental", "Travel Insurance", "Airbnb Stay", "Travel Agency Package")),
            Map.entry("Transport", List.of("Uber", "Train Ticket", "Gas Station", "Parking Fee", "Electric Scooter Rental", "Highway Toll", "Airport Duty Free", "Public Transport Pass", "Car Wash", "Bike Rental", "Luggage Storage")),
            Map.entry("Cyber", List.of("DarkWeb Entry", "Crypto Exchange", "VPN Service")),
            Map.entry("Health", List.of("Pharmacy", "Doctor Visit", "Yoga Class", "Health Insurance", "Vitamin Store", "Therapy Session", "Dental Checkup", "Optician Visit", "Massage Therapy", "Personal Trainer Session"))
    );

    public static final record Range(double min, double max) {

    }

    public static final Map<String, Range> CATEGORYRANGES = Map.ofEntries(
            Map.entry("Food", new Range(12.0, 120.0)),
            Map.entry("Shopping", new Range(30.0, 600.0)),
            Map.entry("Clothes", new Range(25.0, 450.0)),
            Map.entry("Beauty", new Range(10.0, 150.0)),
            Map.entry("Entertainment", new Range(10.0, 250.0)),
            Map.entry("Subscriptions", new Range(5.0, 40.0)),
            Map.entry("Utilities", new Range(20.0, 350.0)),
            Map.entry("Travel", new Range(100.0, 1500.0)),
            Map.entry("Transport", new Range(2.0, 60.0)),
            Map.entry("Cyber", new Range(500.0, 10000.0)),
            Map.entry("Health", new Range(15.0, 300.0))
    );

    public static final Map<String, Double> FIXEDPRICES = Map.ofEntries(
            Map.entry("Netflix", 17.99),
            Map.entry("Spotify", 10.99),
            Map.entry("Disney+", 8.99),
            Map.entry("LinkedIn Premium", 34.99),
            Map.entry("ChatGPT Plus", 20.00),
            Map.entry("Amazon Prime", 4.99),
            Map.entry("HBO Max", 9.99),
            Map.entry("Apple Music", 10.99),
            Map.entry("Audible Subscription", 9.95),
            Map.entry("Online Newspaper", 12.00)
    );

    public static final List<String> BLACKLISTED_KEYWORDS = List.of(
        "DarkWeb", "Hacker", "Exploit", "Bypass", "SQL Injection", "Crypto Mixer"
    );
}
