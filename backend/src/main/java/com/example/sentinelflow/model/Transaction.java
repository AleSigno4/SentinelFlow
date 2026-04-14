package com.example.sentinelflow.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
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
    private BigDecimal amount;
    private String description;
    private String category;
    private LocalDateTime timestamp;

    public Transaction(BigDecimal amount, String description, String category, LocalDateTime timestamp) {
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.timestamp = timestamp;
    }
}
