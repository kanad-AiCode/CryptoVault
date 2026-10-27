package com.kanad.cryptovault.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.PositiveOrZero;

@Entity

public class WalletEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long walletId;
	
	@NotBlank(message="walletAddress required!")
	@Column(nullable=false,unique=true)
	private String walletAddress;
	
	@NotBlank(message="cryptocurrency is must required")
	private String cryptocurrency;
	
	
	@NotNull(message="balance is required")
	@PositiveOrZero(message="balance cannot be negative")
	private BigDecimal balance;
	
	@NotNull(message="lockedBalance is required")
	@PositiveOrZero(message="lockedBalance cannot be negative")
	private BigDecimal lockedBalance=BigDecimal.ZERO;
	
	
	
	public BigDecimal getAvailableBalance() {
	
		if(balance==null || lockedBalance==null) {
			
			return BigDecimal.ZERO;
		}
		
		BigDecimal result=balance.subtract(lockedBalance);
		return result;
	}
	
	
	public BigDecimal getLockedBalance() {
		return lockedBalance;
	}

	public void setLockedBalance(BigDecimal lockedBalance) {
		this.lockedBalance = lockedBalance;
	}

	private String status="ACTIVE";
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	
	
	public Long getWalletId() {
		return walletId;
	}

	public void setWalletId(Long walletId) {
		this.walletId = walletId;
	}

	public String getWalletAddress() {
		return walletAddress;
	}

	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}

	public String getCryptocurrency() {
		return cryptocurrency;
	}

	public void setCryptocurrency(String cryptocurrency) {
		this.cryptocurrency = cryptocurrency;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	@PrePersist

	protected void setCreatedAt() {
		this.createdAt = LocalDateTime.now();
	}
	
	
	public UserEntity getUser() {
	    return user;
	}

	public void setUser(UserEntity user) {
	    this.user = user;
	}
	
	

}
