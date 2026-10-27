package com.kanad.cryptovault.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ExternalPaymentRequest {

	
	@NotNull(message="SenderWalletId cannot be empty")
	private Long senderWalletId;
	
	@NotBlank(message="ExternalReceiverAddress cannot be empty")
	private String externalReceiverAddress;
	
	@NotNull(message="amount cannot be empty")
	@Positive(message="amount cannot be negative")
	private BigDecimal amount;
	
	@NotBlank(message="Cryptocurrency must be get filled")
	private String cryptocurrency;
	
	@NotBlank(message = "idempotencyKey is required")
	private String idempotencyKey;

	
	
	
	public Long getSenderWalletId() {
		return senderWalletId;
	}

	public void setSenderWalletId(Long senderWalletId) {
		this.senderWalletId = senderWalletId;
	}

	public String getExternalReceiverAddress() {
		return externalReceiverAddress;
	}

	public void setExternalReceiverAddress(String externalReceiverAddress) {
		this.externalReceiverAddress = externalReceiverAddress;
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

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}
	
	
	
	
}
