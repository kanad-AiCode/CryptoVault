package com.kanad.cryptovault.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BitcoinTestSendRequest {

    @NotBlank(message = "Receiver address is required")
    private String receiverAddress;

    @NotNull(message = "Amount is required")
    @DecimalMin(
            value = "0.00000001",
            message = "Amount must be at least 0.00000001 BTC")
    @DecimalMax(
            value = "0.0001",
            message = "Test amount must not exceed 0.0001 BTC")
    private BigDecimal amount;

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}