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
import org.springframework.web.bind.annotation.RestController;

import com.kanad.cryptovault.request.DepositRequest;
import com.kanad.cryptovault.response.TransactionResponse;
import com.kanad.cryptovault.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
	
	@Autowired
	private TransactionService transactionService;
	
	
	@PostMapping("/{walletId}/deposit")
	
	public ResponseEntity<String > depositCrypto(@PathVariable Long walletId,@Valid @RequestBody DepositRequest request ){
		
		
		transactionService.depositCrypto(walletId,request.getAmount());
		return ResponseEntity.status(HttpStatus.OK).body("Amount Deposited Successfully!");
	}
	
	@PostMapping("/{walletId}/withdraw")
	
	public ResponseEntity<String> withdrawCrypto(@PathVariable Long walletId,@Valid @RequestBody DepositRequest request){
		
		
		transactionService.withdrawCrypto(walletId, request.getAmount());
		return ResponseEntity.status(HttpStatus.OK).body("Amount Withdrawn Successfully!");
	
	}
	@GetMapping("/{transactionId}/byId")
	
	public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long transactionId){
		
		TransactionResponse transactionResponseForUser=transactionService.getTransactionById(transactionId);
		
		return ResponseEntity.status(HttpStatus.OK).body(transactionResponseForUser);
		
	}
	
	@GetMapping()
	public ResponseEntity<List<TransactionResponse>> getAllTransaction(){
		
		List<TransactionResponse> transactionHistory=transactionService.getAllTransaction();
		
		return ResponseEntity.status(HttpStatus.OK).body(transactionHistory);
	}
	
	
	@GetMapping("wallet/{walletId}")
	
	public ResponseEntity<List<TransactionResponse>> getTransactionsByWalletId(@PathVariable Long walletId){
		
		List<TransactionResponse> transactionHistoryByWalletId=transactionService.getTransactionsByWalletId(walletId);
		
		return ResponseEntity.status(HttpStatus.OK).body(transactionHistoryByWalletId);
		
		
	}

}
