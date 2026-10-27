package com.kanad.cryptovault.service;

import java.math.BigDecimal;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kanad.cryptovault.entity.BlockchainTransactionStatus;
import com.kanad.cryptovault.entity.PaymentEntity;
import com.kanad.cryptovault.entity.PaymentStatus;
import com.kanad.cryptovault.entity.PaymentType;
import com.kanad.cryptovault.entity.WalletEntity;
import com.kanad.cryptovault.exception.PaymentNotFoundException;
import com.kanad.cryptovault.exception.PaymentStatusException;
import com.kanad.cryptovault.exception.PaymentTypeException;
import com.kanad.cryptovault.exception.TransactionNullException;
import com.kanad.cryptovault.repository.PaymentRepository;
import com.kanad.cryptovault.repository.WalletRepository;

@Service
public class BlockchainConfirmationProcessor {
	
	private final PaymentRepository repository;
    private final BlockchainService blockchainService;
    private final WalletRepository walletRepository;

    public BlockchainConfirmationProcessor(PaymentRepository repository,
    		BlockchainService blockchainService,
    		WalletRepository walletRepository) {
    	
    	this.repository=repository;
    	this.blockchainService=blockchainService;
    	this.walletRepository=walletRepository;
    }

    
    @Transactional
   public BlockchainTransactionStatus checkPaymentConfirmation(Long paymentId) {
	   
	   PaymentEntity broadcastedPayment = repository.findById(paymentId)
	            .orElseThrow(() ->
	                    new PaymentNotFoundException(
	                            "Payment not found with ID: " + paymentId));

	   
	   
	   if(PaymentType.EXTERNAL != broadcastedPayment.getPaymentType()) {
		   throw new PaymentTypeException(
	                "Payment is internal and can't get checked on blockchain");
		   
	   }
	   
	   if(PaymentStatus.BROADCASTED !=broadcastedPayment.getStatus()) {
		   
		   throw new PaymentStatusException(
	                "Payment status should be brodcasted");
	   }
	   
	   if(broadcastedPayment.getTransactionHash()==null) {
		   
		   throw new TransactionNullException("Transaction hash is required for confirmation checking.");
	   }
	   
	   
	   
	   BlockchainTransactionStatus  status=blockchainService.getTransactionStatus(broadcastedPayment.getTransactionHash());
	   
	   
	   if(status==BlockchainTransactionStatus.CONFIRMED) {
		   
		   WalletEntity senderWallet=broadcastedPayment.getSenderwallet();
		  
		   
		   BigDecimal newLockedBalance=senderWallet.getLockedBalance().subtract(broadcastedPayment.getAmount());
		   
		   BigDecimal newBalance=senderWallet.getBalance().subtract(broadcastedPayment.getAmount());
		   
		   senderWallet.setBalance(newBalance);
		   senderWallet.setLockedBalance(newLockedBalance);
		   broadcastedPayment.setStatus(PaymentStatus.COMPLETED);
		   walletRepository.save(senderWallet);
		   repository.save(broadcastedPayment);
	   }
	   
	   
	   else if(status == BlockchainTransactionStatus.PENDING) {
		   
		  
	   }
	   
	   else if(status ==BlockchainTransactionStatus.FAILED) {
		   
		   WalletEntity senderWallet=broadcastedPayment.getSenderwallet();
		 
		   
		   BigDecimal newLockedBalance=senderWallet.getLockedBalance().subtract(broadcastedPayment.getAmount());
		  
		  senderWallet.setLockedBalance(newLockedBalance);
		  broadcastedPayment.setStatus(PaymentStatus.FAILED);
          broadcastedPayment.setFailureReason(
                  "Blockchain transaction failed");

          walletRepository.save(senderWallet);
          repository.save(broadcastedPayment);
		   
	   }
	   
	   else if(status==BlockchainTransactionStatus.NOT_FOUND) {
		  
		   
	   }
	   
	   return status;
   }
}
