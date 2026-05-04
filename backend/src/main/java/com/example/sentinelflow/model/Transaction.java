package com.example.sentinelflow.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "transactions")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Double amount;
    private String description;
    private String category;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    private LocalDateTime timestamp;
    private Double riskScore;
    private String aiReason;

    public Transaction(Long userId, Double amount, String description, String category, TransactionStatus status, LocalDateTime timestamp, Double riskScore, String aiReason) {
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.status = status;
        this.timestamp = timestamp;
        this.riskScore = riskScore;
        this.aiReason = aiReason;
    }
}
