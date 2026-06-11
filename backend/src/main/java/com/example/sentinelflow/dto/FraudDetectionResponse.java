package com.example.sentinelflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FraudDetectionResponse {

    private int prediction;

    @JsonProperty("is_fraud")
    private boolean isFraud;

    public int getPrediction() {
        return prediction;
    }

    public boolean isFraud() {
        return isFraud;
    }
}