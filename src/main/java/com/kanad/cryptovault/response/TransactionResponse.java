package com.kanad.cryptovault.response;

import java.math.BigDecimal;

import com.kanad.cryptovault.entity.TransactionType;

public class TransactionResponse {
	
	private Long transactionId;
	private BigDecimal amount;
	private TransactionType type;
	private TransactionWalletResponse wallet;
	
	
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
	public TransactionWalletResponse getWallet() {
		return wallet;
	}
	public void setWallet(TransactionWalletResponse wallet) {
		this.wallet = wallet;
	}

}
