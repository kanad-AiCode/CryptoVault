package com.kanad.cryptovault.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.kanad.cryptovault.event.ExternalPaymentCreatedEvent;


@Service
public class ExternalPaymentEventListener {
	
	private final ExternalPaymentProcessor processor;

	public ExternalPaymentEventListener (ExternalPaymentProcessor processor) {
		
		this.processor=processor;
	}
	
	  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	    
	    public void handleExternalPaymentCreated(
	            ExternalPaymentCreatedEvent event) {

	       processor.processExternalPayment(event.getPaymentId());
	    }
	  
	  
}
