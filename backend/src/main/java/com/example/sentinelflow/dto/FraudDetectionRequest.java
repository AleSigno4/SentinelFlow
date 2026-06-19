package com.example.sentinelflow.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FraudDetectionRequest {

    @NotNull(message = "Amount cannot be null")
    @DecimalMin("0.01")
    public double amount;

    @NotNull(message = "Category cannot be null")
    public int category;

     @NotBlank(message = "Description cannot be blank")
    public String description;

    @Min(0)
    @Max(23)
    public int hour;

    @PositiveOrZero
    @JsonProperty("tx_count_3min")
    public int txCount3min;

    @PositiveOrZero
    @JsonProperty("time_diff")
    public double timeDiff;

    public FraudDetectionRequest(double amount, int category, String description,
                                  int hour, int txCount3min, double timeDiff) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.hour = hour;
        this.txCount3min = txCount3min;
        this.timeDiff = timeDiff;
    }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public Integer getCategory() { return category; }
    public void setCategory(Integer category) { this.category = category; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getHour() { return hour; }
    public void setHour(Integer hour) { this.hour = hour; }
    
    public Integer getTx_count_3min() { return txCount3min; }
    public void setTx_count_3min(Integer tx_count_3min) { this.txCount3min = tx_count_3min; }
    
    public Double getTime_diff() { return timeDiff; }
    public void setTime_diff(Double time_diff) { this.timeDiff = time_diff; }
}