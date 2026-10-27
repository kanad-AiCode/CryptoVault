package com.kanad.cryptovault.entity;

import java.math.BigDecimal;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


@Entity
public class TransactionEntity {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long transactionId;
	
	@NotNull(message="amount cannot be null")
	@Positive(message = "amount must be greater than zero")
	private BigDecimal amount;
	
	@Enumerated(EnumType.STRING)
	private TransactionType type;
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	@ManyToOne
	@JoinColumn(name = "wallet_id", nullable = false)
	private WalletEntity wallet;
	
	@Enumerated(EnumType.STRING)
	private TransactionSource source;
	
	
	
	
	
	public TransactionSource getSource() {
		return source;
	}
	public void setSource(TransactionSource source) {
		this.source = source;
	}
	public Long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public TransactionType getType() {
		return type;
	}
	public void setType(TransactionType type) {
		this.type = type;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}  
	@PrePersist
	protected void setCreatedAt() {
	    this.createdAt = LocalDateTime.now();
	}
	public WalletEntity getWallet() {
		return wallet;
	}
	public void setWallet(WalletEntity wallet) {
		this.wallet = wallet;
	}

}
