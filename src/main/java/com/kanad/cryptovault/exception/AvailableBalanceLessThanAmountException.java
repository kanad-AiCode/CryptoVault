package com.kanad.cryptovault.exception;



public class AvailableBalanceLessThanAmountException extends RuntimeException {
	
	public AvailableBalanceLessThanAmountException(String message) {
		
		super(message);
	}

}
