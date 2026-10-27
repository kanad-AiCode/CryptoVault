package com.kanad.cryptovault.exception;

public class UserNotFoundByEmailException extends RuntimeException {
	
	public UserNotFoundByEmailException(String message) {
		
		super(message);
	}

}
