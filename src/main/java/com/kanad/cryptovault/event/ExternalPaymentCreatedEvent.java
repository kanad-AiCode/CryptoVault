package com.kanad.cryptovault.event;

public class ExternalPaymentCreatedEvent {
	
	
	  private final Long paymentId;

	    public ExternalPaymentCreatedEvent(Long paymentId) {
	        this.paymentId = paymentId;
	    }

	    public Long getPaymentId() {
	        return paymentId;
	    }
}
