package com.example.sentinelflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FraudDetectionRequest {

    public double amount;
    public int category;
    public String description;
    public int hour;

    @JsonProperty("tx_count_3min")
    public int txCount3min;

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
}