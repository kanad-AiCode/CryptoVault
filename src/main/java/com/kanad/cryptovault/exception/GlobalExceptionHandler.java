package com.kanad.cryptovault.exception;

import java.util.HashMap;





import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kanad.cryptovault.response.ValidationErrorResponse;



@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<String> handleEmailAlreadyExists(EmailAlreadyExistsException ex){
		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}
	
	
	@ExceptionHandler(UserNotFoundByEmailException.class)
	
	public ResponseEntity<String> handleUserNotFoundByEmail(UserNotFoundByEmailException ex){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		
	}
	
	
	@ExceptionHandler(UserNotFoundByIdException.class)
	public ResponseEntity<String> handleUserNotFoundById(UserNotFoundByIdException ex){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		
	}
	
	
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorResponse<Map<String, String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex){
		
		
		Map<String,String> errors=new HashMap<>();
		
		ex.getBindingResult().getFieldErrors().forEach(error -> {
	        errors.put(error.getField(), error.getDefaultMessage());
	    });
		
		ValidationErrorResponse<Map<String,String>> response= new ValidationErrorResponse("Validation failed",errors);
		
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(response);
		
	}
	
	
	@ExceptionHandler(WalletAddressAlreadyExistsException.class)
	
	public ResponseEntity<String> handleWalletAlreadyeExists(WalletAddressAlreadyExistsException ex){
		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}
	
	
	@ExceptionHandler(WalletNotFoundByIdException.class)
	
	public ResponseEntity<String> handleWalletNotFound(WalletNotFoundByIdException ex){
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		
	}
	
	@ExceptionHandler(WalletNotFoundByAddressException.class)
	
	public ResponseEntity<String> handleWalletNotFound(WalletNotFoundByAddressException ex){
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	
	@ExceptionHandler(WalletsNotFoundException.class)
	
	public ResponseEntity<String> handleWalletsNotFound(WalletsNotFoundException ex){
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	
	
	@ExceptionHandler(InsufficientBalanceException.class)
	
	public ResponseEntity<String> handleInsufficientBalance (InsufficientBalanceException ex){
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
	
	@ExceptionHandler(TransactionNotFoundException.class)
	
	public ResponseEntity<String> handleTransactionNotFound(TransactionNotFoundException ex){
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	
	
	@ExceptionHandler(SameWalletException.class)
	
	public ResponseEntity<String> handleSameWallet(SameWalletException ex){
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
	
	@ExceptionHandler(DifferentCryptocurrencyException.class)
	
	public ResponseEntity<String> handleDifferentCryptocurrency(DifferentCryptocurrencyException ex){
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
	
	
	@ExceptionHandler(PaymentNotFoundException.class)
		public ResponseEntity<String> handlePaymentNotFound(PaymentNotFoundException ex){
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
	
	
	@ExceptionHandler(PaymentsNotAvailableException.class)
	
	public ResponseEntity<String> handlePaymentNotAvailable(PaymentsNotAvailableException ex){
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	
	
	@ExceptionHandler(DuplicatePaymentException.class)
	public ResponseEntity<String> handleDuplicatePayment(DuplicatePaymentException ex) {
	    return ResponseEntity
	            .status(HttpStatus.CONFLICT)
	            .body(ex.getMessage());
	}
	
	
	
	
	
	
	@ExceptionHandler(CryptocurrencyNullException.class)
	
	public ResponseEntity<String> handleCryptocurrencyNull(CryptocurrencyNullException ex){
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
	@ExceptionHandler(AmountLessThanZeroException.class)
	
	public ResponseEntity<String> handleAmountLessThanZero(AmountLessThanZeroException ex){
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
	@ExceptionHandler(ReceiverAddressEmptyException.class)
	
	public ResponseEntity<String> handleReceiverAddressEmpty(ReceiverAddressEmptyException ex){
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
	@ExceptionHandler(AvailableBalanceLessThanAmountException.class)
	
	public ResponseEntity <String> handleAvailableBalanceLessThanAmount(AvailableBalanceLessThanAmountException ex){
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
	
	
	@ExceptionHandler(PaymentTypeException.class)
	public ResponseEntity<String> handlePaymentType(PaymentTypeException ex){
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		
	}
	
	@ExceptionHandler(PaymentStatusException.class)
	public ResponseEntity<String> handlePaymentStatus(PaymentStatusException ex){
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
	
	@ExceptionHandler(TransactionNullException.class)
	public ResponseEntity<String> handleTransactionNull(TransactionNullException ex){
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
}
