package com.kanad.cryptovault.response;

import java.math.BigDecimal;

public class WalletResponse {

    private Long walletId;
    private String walletAddress;
    private String cryptocurrency;
    private BigDecimal balance;
    private String status;
    private UserResponse user;
    
    
    
    
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
	public UserResponse getUser() {
		return user;
	}
	public void setUser(UserResponse user) {
		this.user = user;
	}

}