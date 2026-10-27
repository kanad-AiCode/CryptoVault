package com.kanad.cryptovault.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kanad.cryptovault.entity.BlockchainTransactionStatus;
import com.kanad.cryptovault.request.ExternalPaymentRequest;
import com.kanad.cryptovault.request.PaymentRequest;
import com.kanad.cryptovault.response.PaymentResponse;
import com.kanad.cryptovault.service.BlockchainConfirmationProcessor;
import com.kanad.cryptovault.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payments")
public class PaymentController {
	
	
	@Autowired
	private PaymentService service;
	
	@Autowired
	private  BlockchainConfirmationProcessor confirmationProcessor;
	@PostMapping("/internalTransfer")
	public ResponseEntity<PaymentResponse> internalCryptoPay(@Valid @RequestBody PaymentRequest request){
		
		PaymentResponse responseToUser=service.transferCrypto(request);
		
		return ResponseEntity.status(HttpStatus.OK).body(responseToUser);
		
		
	}
	
	@PostMapping("/externalTransfer")
	public ResponseEntity<PaymentResponse> externalCryptoPay(@Valid @RequestBody ExternalPaymentRequest request){
		
		
		PaymentResponse responseToUser=service.createExternalPayment(request);
		
		return ResponseEntity.status(HttpStatus.OK).body(responseToUser);
	}

	
	@GetMapping("/getById")
	public ResponseEntity<PaymentResponse> getPaymentById(@RequestParam Long paymentId){
		
		
		PaymentResponse getById=service.getPaymentById(paymentId);
		return ResponseEntity.status(HttpStatus.OK).body(getById);
	}
	
	
	@GetMapping("/allPayments")
	public ResponseEntity<List<PaymentResponse>> getPayments(){
		
		List<PaymentResponse> payments=service.getAllPayment();
		return ResponseEntity.status(HttpStatus.OK).body(payments);
	}
	
	@GetMapping("/wallet/{walletId}")
	
	public ResponseEntity<List<PaymentResponse>> getPaymentsByWalletId(@PathVariable Long walletId){
		
		List<PaymentResponse> payments=service.getPaymentsByWalletId(walletId);
		
		return ResponseEntity.status(HttpStatus.OK).body(payments);
	}
	
	
	@GetMapping("/{paymentId}/confirmation")
	public ResponseEntity<BlockchainTransactionStatus> checkPaymentConfirmation(
	        @PathVariable Long paymentId) {

	    BlockchainTransactionStatus status =
	            confirmationProcessor.checkPaymentConfirmation(paymentId);

	    return ResponseEntity.ok(status);
	}
}
