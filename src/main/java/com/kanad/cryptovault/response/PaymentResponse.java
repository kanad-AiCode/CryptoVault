package com.kanad.cryptovault.response;

import java.math.BigDecimal;

import java.time.LocalDateTime;

import com.kanad.cryptovault.entity.PaymentStatus;
import com.kanad.cryptovault.entity.PaymentType;

public class PaymentResponse {

	
	private Long paymentId;
	private Long senderWalletId;
	private Long receiverWalletId;
	private BigDecimal amount;
	private String cryptoCurrency;
	private PaymentStatus status;
	private String reference;   
	private LocalDateTime createdAt;
	private String idempotencyKey;
	
	private PaymentType paymentType;
	private String externalReceiverAddress;
	
	private String transactionHash;
	
	 public String getTransactionHash() {
		return transactionHash;
	}
	 public void setTransactionHash(String transactionHash) {
		 this.transactionHash = transactionHash;
	 }
	 public String getIdempotencyKey() {
		return idempotencyKey;
	}
	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}
	 public Long getPaymentId() {
		 return paymentId;
	 }
	 public void setPaymentId(Long paymentId) {
		 this.paymentId = paymentId;
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
	 public String getCryptoCurrency() {
		 return cryptoCurrency;
	 }
	 public void setCryptoCurrency(String cryptoCurrency) {
		 this.cryptoCurrency = cryptoCurrency;
	 }
	 public PaymentStatus getStatus() {
		 return status;
	 }
	 public void setStatus(PaymentStatus status) {
		 this.status = status;
	 }
	 public String getReference() {
		 return reference;
	 }
	 public void setReference(String reference) {
		 this.reference = reference;
	 }
	 public LocalDateTime getCreatedAt() {
		 return createdAt;
	 }
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt=createdAt;
	}
	public PaymentType getPaymentType() {
	    return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
	    this.paymentType = paymentType;
	}
	public String getExternalReceiverAddress() {
		return externalReceiverAddress;
	}
	public void setExternalReceiverAddress(String externalReceiverAddress) {
		this.externalReceiverAddress = externalReceiverAddress;
	}
	 
	 
}
