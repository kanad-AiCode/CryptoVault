package com.kanad.cryptovault.exception;



public class WalletAddressAlreadyExistsException extends RuntimeException {
	
	public WalletAddressAlreadyExistsException(String message){
		
		super(message);
	}

}
