package com.kanad.cryptovault.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kanad.cryptovault.entity.UserEntity;
import com.kanad.cryptovault.entity.WalletEntity;
import com.kanad.cryptovault.exception.UserNotFoundByIdException;
import com.kanad.cryptovault.exception.WalletAddressAlreadyExistsException;
import com.kanad.cryptovault.exception.WalletNotFoundByAddressException;
import com.kanad.cryptovault.exception.WalletNotFoundByIdException;
import com.kanad.cryptovault.exception.WalletsNotFoundException;
import com.kanad.cryptovault.repository.UserRepository;
import com.kanad.cryptovault.repository.WalletRepository;
import com.kanad.cryptovault.request.WalletRequest;
import com.kanad.cryptovault.response.WalletResponse;

@Service
public class WalletService {

	
	@Autowired
	private WalletRepository walletRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Autowired
	private UserService userService;
	
	public WalletResponse registerWallet(WalletRequest request,Long userId) {
		
		WalletEntity wallet=convertToEntity(request);
		
		
		UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(
                        "User not found with id: " + userId
                ));
		
		
		
		wallet.setUser(user);
		WalletEntity savedWallet=walletRepository.save(wallet);
		
		return convertToResponse(savedWallet);
	
	}
	
	public WalletResponse getWalletById(Long walletId) {
		

		WalletEntity wallet = walletRepository.findByWalletId(walletId)
	            .orElseThrow(() ->
	                new WalletNotFoundByIdException(
	                    "Wallet not found with ID: " + walletId));

	    return convertToResponse(wallet);
		
		
	}
	
	public WalletResponse getWalletByAdd(String walletAdd) {
		

	
	WalletEntity wallet=walletRepository.findByWalletAddress(walletAdd)
			.orElseThrow(() ->  new WalletNotFoundByAddressException(" Wallet not found with address: " + walletAdd));
	
	
	return convertToResponse(wallet);
	
	}
	
	public List<WalletResponse> getAllWallet(){
		
		 List<WalletEntity> wallets = walletRepository.findAll();
		    if (wallets.isEmpty()) {
		        throw new WalletsNotFoundException("No wallets found");
		    }
		    
		    List<WalletResponse> allWallets=new ArrayList<>();
		    
		    for(WalletEntity wallet:wallets) {
		    	
		    	allWallets.add(convertToResponse(wallet));
		    }
		    
		    
		    return allWallets;
			
	}
	
	
	public List<WalletResponse> getAllWalletsByUser(Long userId){
		
		
		if(!userRepository.existsById(userId)) {
			
			throw new UserNotFoundByIdException("User not found with ID : " + userId);
		}
		
		List<WalletEntity> wallets=walletRepository.findByUser_Id(userId);
		
		if(wallets.isEmpty()) {
			
			throw new WalletsNotFoundException(
				    "No wallets found for user ID: " + userId
				);
			
		}
		
		
		List<WalletResponse> allWallets=new ArrayList<>();
		for(WalletEntity wallet:wallets) {
			
			allWallets.add(convertToResponse(wallet));
			
			
			
		}
		return allWallets;
	}
	
	public void deleteWalletById(Long walletId) {
		
		Optional<WalletEntity> wallet=walletRepository.findByWalletId(walletId);
		
		if(wallet.isEmpty()) {
			 throw new WalletNotFoundByIdException(
	                "Wallet not found with ID: " + walletId);
			
			
			
		}
		
	    walletRepository.deleteById(walletId);
		
		
	}
	
	public WalletResponse convertToResponse(WalletEntity wallet) {

	   WalletResponse response=new WalletResponse();
	   
	   
	   
	   response.setUser(userService.convertToResponse(wallet.getUser()));
	   response.setBalance(wallet.getBalance());
	   response.setCryptocurrency(wallet.getCryptocurrency());
	   response.setWalletAddress(wallet.getWalletAddress());
	   response.setWalletId(wallet.getWalletId());
	   response.setStatus(wallet.getStatus());
	   
	   
	   return response;
	}
	
	
	public WalletEntity convertToEntity(WalletRequest request) {
		
		WalletEntity entityRequest=new WalletEntity();
		
		entityRequest.setCryptocurrency(request.getCryptocurrency());
		entityRequest.setWalletAddress(request.getWalletAddress());
		entityRequest.setBalance(request.getBalance());
		
		
		return entityRequest;
		
	}
	
	
}
