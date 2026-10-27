package com.kanad.cryptovault.service;

import java.math.BigDecimal;


import com.kanad.cryptovault.entity.BlockchainTransactionStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface BlockchainService {

	
	
	
	public String sendPayment(
			 @NotBlank(message = "Cryptocurrency cannot be empty")
	            String cryptocurrency,

	            @NotNull(message = "Amount cannot be null")
	            @Positive(message = "Amount must be greater than zero")
	            BigDecimal amount,

	            @NotBlank(message = "Receiver address cannot be empty")
	            String receiverAddress
		);
	

   public BlockchainTransactionStatus getTransactionStatus(String transactionHash) ;
		
		
		
		

	
}
