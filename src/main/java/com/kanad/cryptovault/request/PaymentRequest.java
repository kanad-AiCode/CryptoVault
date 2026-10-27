package com.kanad.cryptovault.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PaymentRequest {
	
	@NotNull(message="SenderWalletId cannot be empty")
	private Long senderWalletId;
	
	@NotNull(message="ReceiverWalletId cannot be empty")
	private Long receiverWalletId;
	
	@NotNull(message=" Amount cannot be empty")
	@Positive(message="Amount must be greater than 0")
	private BigDecimal amount;
	
	@NotBlank(message="Cryptocurrency must be get filled")
	private String cryptocurrency;
	
	@NotBlank(message = "idempotencyKey is required")
	private String idempotencyKey;
	

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	public Long getSenderWalletId() {
		return senderWalletId;
	}

	public void setSenderWalletId(Long senderWalletId) {
		this.senderWalletId = senderWalletId;
	}

	public Long getReceiverWalletId() {
		return receiverWalletId;
	}

	public void setReceiverWalletId(Long receiverWalletId) {
		this.receiverWalletId = receiverWalletId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCryptocurrency() {
		return cryptocurrency;
	}

	public void setCryptocurrency(String cryptocurrency) {
		this.cryptocurrency = cryptocurrency;
	}
}
