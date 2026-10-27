package com.kanad.cryptovault.response;

import java.math.BigDecimal;

import com.kanad.cryptovault.entity.TransactionType;


public class TransactionWalletResponse {

	
	private BigDecimal balance;
	private String cryptocurrency;
	private String walletAddress;
	private UserResponse user;
	
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public String getCryptocurrency() {
		return cryptocurrency;
	}
	public void setCryptocurrency(String cryptocurrency) {
		this.cryptocurrency = cryptocurrency;
	}
	public String getWalletAddress() {
		return walletAddress;
	}
	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}
	public UserResponse getUser() {
		return user;
	}
	public void setUser(UserResponse user) {
		this.user = user;
	}
	
	
	
}
