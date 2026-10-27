package com.kanad.cryptovault.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class WalletRequest {
	
	@NotBlank(message = "walletAddress required!")
    private String walletAddress;

    @NotBlank(message = "cryptocurrency is required")
    private String cryptocurrency;

    @NotNull(message = "balance is required")
    @PositiveOrZero(message = "balance cannot be negative")
    private BigDecimal balance;

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

}
