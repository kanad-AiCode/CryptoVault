

package com.kanad.cryptovault.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kanad.cryptovault.entity.WalletEntity;
import com.kanad.cryptovault.repository.WalletRepository;
import com.kanad.cryptovault.request.WalletRequest;
import com.kanad.cryptovault.response.WalletResponse;
import com.kanad.cryptovault.service.WalletService;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/wallets")
public class WalletController {
	
	@Autowired
	private WalletService walletService;
	
	@PostMapping()
   public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody WalletRequest wallet,@RequestParam Long userId){
	
	WalletResponse newWallet=walletService.registerWallet(wallet, userId);
	
	return ResponseEntity.status(HttpStatus.CREATED).body(newWallet);
	
	
	
}
	
	
	@GetMapping("/{walletId}")
	
	public ResponseEntity<WalletResponse> getWalletById(@PathVariable Long walletId){
		
       WalletResponse wallet=walletService.getWalletById(walletId);
		
		return ResponseEntity.status(HttpStatus.OK).body(wallet);
		
	}
	
	@GetMapping("/address/{walletAdd}")
	public ResponseEntity<WalletResponse> getWalletByAdd(@PathVariable String walletAdd){
		
		WalletResponse wallet=walletService.getWalletByAdd(walletAdd);
		  
		return ResponseEntity.status(HttpStatus.OK).body(wallet);
	}

	
	@GetMapping()
	public ResponseEntity<List<WalletResponse>> getAllWallets(){
		
		List<WalletResponse> allWallets=walletService.getAllWallet();
		
		return ResponseEntity.status(HttpStatus.OK).body(allWallets);
		
	}
	
	@GetMapping("/user/{userId}")
	
	public ResponseEntity<List<WalletResponse>> getWalletsByUser(@PathVariable Long userId){
		
		List<WalletResponse> allWallets=walletService.getAllWalletsByUser(userId);
		return ResponseEntity.status(HttpStatus.OK).body(allWallets);
	}
	
	@DeleteMapping("/delete/{walletId}")
	public ResponseEntity<String> deleteWalletById(@PathVariable Long walletId){
		
		walletService.deleteWalletById(walletId);
		return ResponseEntity.status(HttpStatus.OK).body("wallet deleted successfully!");
		
	}
}
