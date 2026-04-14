package com.example.sentinelflow.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sentinelflow.model.Transaction;
import com.example.sentinelflow.repository.TransactionRepository;



@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public Iterable<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }
    
    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction entity) {
        this.transactionRepository.save(entity);
        return entity;
    }
    
}
