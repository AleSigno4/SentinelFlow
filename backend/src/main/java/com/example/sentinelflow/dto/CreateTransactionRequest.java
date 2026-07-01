package com.example.sentinelflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateTransactionRequest {

    @NotNull(message = "userId cannot be null")
    private Long userId;

    @NotNull(message = "amount cannot be null")
    @DecimalMin(value = "0.01", message = "amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "description cannot be blank")
    private String description;

    @NotBlank(message = "category cannot be blank")
    private String category;

    public CreateTransactionRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}