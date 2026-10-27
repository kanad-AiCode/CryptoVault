package com.kanad.cryptovault.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.kanad.cryptovault.entity.PaymentEntity;
import com.kanad.cryptovault.entity.PaymentStatus;
import com.kanad.cryptovault.entity.PaymentType;
import com.kanad.cryptovault.entity.WalletEntity;
import com.kanad.cryptovault.event.ExternalPaymentCreatedEvent;
import com.kanad.cryptovault.exception.BlockchainPaymentException;
import com.kanad.cryptovault.exception.PaymentNotFoundException;
import com.kanad.cryptovault.exception.PaymentStatusException;
import com.kanad.cryptovault.exception.PaymentTypeException;
import com.kanad.cryptovault.repository.PaymentRepository;
import com.kanad.cryptovault.repository.WalletRepository;
import com.kanad.cryptovault.response.PaymentResponse;


@Service
public class ExternalPaymentProcessor {

	 private final PaymentRepository repository;
	    private final BlockchainService blockchainService;
	    private final WalletRepository walletRepository;


	    public ExternalPaymentProcessor(
	            PaymentRepository repository,
	            BlockchainService blockchainService,WalletRepository walletRepository) {

	        		this.repository = repository;
	        		this.blockchainService = blockchainService;
	        		 this.walletRepository = walletRepository;
	    }

	    
	  
	@Async
	@Transactional
public void processExternalPayment(Long paymentId) {

   
    PaymentEntity pendingPayment = repository.findById(paymentId)
            .orElseThrow(() ->
                    new PaymentNotFoundException(
                            "Payment not found with ID: " + paymentId));

   
    if (pendingPayment.getPaymentType() != PaymentType.EXTERNAL) {

        throw new PaymentTypeException(
                "Payment is internal and cannot be processed as external");
    }

  
    if (pendingPayment.getStatus() != PaymentStatus.PENDING) {

        throw new PaymentStatusException(
                "Payment status is not PENDING");
    }

  
    pendingPayment.setStatus(PaymentStatus.PROCESSING);

    repository.save(pendingPayment);


    try {

        String transactionHash = blockchainService.sendPayment(
                pendingPayment.getCryptocurrency(),
                pendingPayment.getAmount(),
                pendingPayment.getExternalReceiverAddress()
        );

        pendingPayment.setTransactionHash(transactionHash);
        pendingPayment.setStatus(PaymentStatus.BROADCASTED);

        repository.save(pendingPayment);
        }
        
    
        catch(BlockchainPaymentException e) {
        	
        	pendingPayment.setStatus(PaymentStatus.FAILED);
        	pendingPayment.setFailureReason(e.getMessage());
        	
        	WalletEntity senderWallet=pendingPayment.getSenderwallet();
        	
        	BigDecimal newLockedBalance=senderWallet.getLockedBalance().subtract(pendingPayment.getAmount());
        	
        	senderWallet.setLockedBalance(newLockedBalance);
        	
        	 walletRepository.save(senderWallet);
             repository.save(pendingPayment);
        }
  
   
   
   
    
    

   
    
}


}
