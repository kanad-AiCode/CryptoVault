package com.kanad.cryptovault.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DepositRequest {
	
	@NotNull(message = "amount is required")
	@Positive(message = "amount must be greater than zero")
	private BigDecimal amount;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	

}
