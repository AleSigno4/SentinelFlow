package com.example.sentinelflow.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sentinelflow.model.Transaction;
import com.example.sentinelflow.model.TransactionStatus;
import com.example.sentinelflow.repository.TransactionRepository;
import com.example.sentinelflow.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionService transactionService, TransactionRepository transactionRepository) {
        this.transactionService = transactionService;
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
    
    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        this.transactionRepository.deleteById(id);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Transaction> patchTransaction(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        TransactionStatus status = TransactionStatus.valueOf(payload.get("status"));
        Transaction updated = transactionService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}
