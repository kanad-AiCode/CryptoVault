package com.kanad.cryptovault.exception;

public class UserNotFoundByIdException extends RuntimeException {
	
	public UserNotFoundByIdException (String message) {
		
		super(message);
	}

}
