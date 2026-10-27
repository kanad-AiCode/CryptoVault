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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.kanad.cryptovault.entity.PaymentType;

@Entity
public class PaymentEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long paymentId;
	
	
	@ManyToOne
	@JoinColumn(name="senders_wallet_id",nullable=false)
	
	private WalletEntity senderWallet;
	
	@ManyToOne
	@JoinColumn(name="receivers_wallet_id")
	private WalletEntity receiverWallet;
	
	
	


	@Column(
		    name = "amount",
		    nullable = false,
		    precision = 19,
		    scale = 8
		)
		@NotNull(message = "amount cannot be null")
		@Positive(message = "amount must be greater than zero")
		private BigDecimal amount;
	
	
	@NotBlank(message="cryptocurrency is must required")
	private String cryptocurrency;
	
	 @Enumerated(EnumType.STRING)
	private PaymentStatus status=PaymentStatus.PENDING;
	 
	@Column(unique = true, nullable = false)
    private String reference;    

	 @Column(updatable = false)
	 private LocalDateTime createdAt;
	 
	 
	 @Column(unique = true, nullable = false)
	 private String idempotencyKey;

	 @Column(unique=true)
	 private String transactionHash;
	 
	 @Enumerated(EnumType.STRING)
	 private PaymentType paymentType;
	 
	 private String failureReason;
	 
	 
	 
	 public String getFailureReason() {
		return failureReason;
	}

	 public void setFailureReason(String failureReason) {
		 this.failureReason = failureReason;
	 }


	 private String externalReceiverAddress;
	 
	 public String getExternalReceiverAddress() { 
		return externalReceiverAddress;
	}

	 public void setExternalReceiverAddress(String externalReceiverAddress) {
		 this.externalReceiverAddress = externalReceiverAddress;
	 }

	 public PaymentType getPaymentType() {
		return paymentType;
	}

	 public void setPaymentType(PaymentType paymentType) {
		 this.paymentType = paymentType;
	 }

	 
	 public String getIdempotencyKey() {
			return idempotencyKey;
		}

		public void setIdempotencyKey(String idempotencyKey) {
			this.idempotencyKey = idempotencyKey;
		}
	 
	 
	 public String getTransactionHash() {
		return transactionHash;
	}

	 public void setTransactionHash(String transactionHash) {
		 this.transactionHash = transactionHash;
	 }

	 public Long getPaymentId() {
		 return paymentId;
	 }

	 public void setPaymentId(Long paymentId) {
		 this.paymentId = paymentId;
	 }

	 public WalletEntity getSenderwallet() {
		 return senderWallet;
	 }

	 public void setSenderwallet(WalletEntity senderwallet) {
		 this.senderWallet = senderwallet;
	 }

	 public WalletEntity getReceiverWallet() {
		 return receiverWallet;
	 }

	 public void setReceiverWallet(WalletEntity receiverWallet) {
		 this.receiverWallet = receiverWallet;
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

	 @PrePersist
	 protected void onCreatedAt() {
		 this. createdAt=LocalDateTime.now();
	 }

	
		public LocalDateTime getCreatedAt() {
		    return createdAt;
		}
		
		
		

}
