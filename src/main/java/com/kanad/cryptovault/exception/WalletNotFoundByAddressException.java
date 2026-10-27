package com.kanad.cryptovault.exception;



public class WalletNotFoundByAddressException extends RuntimeException 
{

	public WalletNotFoundByAddressException (String message) {
		
		super(message);
	}
}
