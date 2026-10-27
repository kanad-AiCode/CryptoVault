package com.kanad.cryptovault.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kanad.cryptovault.entity.TransactionEntity;
import com.kanad.cryptovault.entity.TransactionSource;
import com.kanad.cryptovault.entity.TransactionType;
import com.kanad.cryptovault.entity.WalletEntity;
import com.kanad.cryptovault.exception.InsufficientBalanceException;
import com.kanad.cryptovault.exception.TransactionNotFoundException;
import com.kanad.cryptovault.exception.WalletNotFoundByIdException;
import com.kanad.cryptovault.repository.TransactionRepository;
import com.kanad.cryptovault.repository.WalletRepository;
import com.kanad.cryptovault.request.DepositRequest;
import com.kanad.cryptovault.response.TransactionResponse;
import com.kanad.cryptovault.response.TransactionWalletResponse;
import com.kanad.cryptovault.response.UserResponse;


@Service
public class TransactionService {
	
	@Autowired
	private WalletRepository walletRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	
	@Transactional
	public void depositCrypto(Long walletId,BigDecimal amount) {
		
		WalletEntity wallet=walletRepository.findByWalletIdForUpdate(walletId).orElseThrow(() -> 
		new WalletNotFoundByIdException("wallet not found with ID : " + walletId));
		
		
		wallet.setBalance(wallet.getBalance().add(amount));
		
		TransactionEntity transaction = new TransactionEntity();
		
		transaction.setAmount(amount);
		transaction.setType(TransactionType.DEPOSIT);
		transaction.setWallet(wallet);
		transaction.setSource(TransactionSource.EXTERNAL);
		
		walletRepository.save(wallet);
		transactionRepository.save(transaction);
		
	}
	
	
	@Transactional
	public void withdrawCrypto(Long walletId,BigDecimal amount) {
		
		WalletEntity wallet=walletRepository.findByWalletIdForUpdate(walletId).orElseThrow(() -> 
		new WalletNotFoundByIdException("wallet not found with ID : " + walletId));
		
		
		
		if(wallet.getBalance().compareTo(amount)<0) {
			
			throw new InsufficientBalanceException("Insufficient Balance to withdraw");
			
		}
		
		
		wallet.setBalance(wallet.getBalance().subtract(amount));
		
		TransactionEntity transaction = new TransactionEntity();
		
		transaction.setType(TransactionType.WITHDRAW);
		transaction.setAmount(amount);
		transaction.setWallet(wallet);
		transaction.setSource(TransactionSource.EXTERNAL);
		
		
		walletRepository.save(wallet);
		transactionRepository.save(transaction);
		
		
	}
	
	
	public TransactionResponse getTransactionById(Long transactionId) {
		
		TransactionEntity transaction = transactionRepository.findById(transactionId)
			    .orElseThrow(() -> new TransactionNotFoundException("No transaction found with Id : " + transactionId));

		return convertToResponse(transaction);

		
	}
	
	public List<TransactionResponse> getAllTransaction(){
		
		List<TransactionEntity> transactions=transactionRepository.findAll();
		
		List<TransactionResponse> allTransaction=new ArrayList<>();
		for(TransactionEntity transaction:transactions) {
			
			allTransaction.add(convertToResponse(transaction));
			
			
		}
		
		return allTransaction;
	}
	
	
	public List<TransactionResponse> getTransactionsByWalletId(Long walletId){
		
		WalletEntity wallet=walletRepository.findByWalletId(walletId).orElseThrow(() -> 
		new WalletNotFoundByIdException("wallet not found with ID : " + walletId));
		
		List<TransactionEntity> transactionsByWalletId=transactionRepository.findByWallet_WalletId(walletId);
		
		List<TransactionResponse> allTransactions=new ArrayList<>();
		
		for(TransactionEntity transaction:transactionsByWalletId) {
			
			allTransactions.add(convertToResponse(transaction));
			
		}
		
		return allTransactions;
		
	}
	
	
	public TransactionEntity convertToEntity(DepositRequest request) {
		
		TransactionEntity requestEntity=new TransactionEntity();
		
		requestEntity.setAmount(request.getAmount());
		
		return requestEntity;
		
	}
	
	
	public TransactionResponse convertToResponse (TransactionEntity transaction) {
		
		
		WalletEntity ownerEntity= transaction.getWallet();
		UserResponse user=new UserResponse();
	
		user.setName(ownerEntity.getUser().getName());
		user.setId(ownerEntity.getUser().getId());
		user.setEmail(ownerEntity.getUser().getEmail());
		user.setActive(ownerEntity.getUser().getActive());
		user.setPhone(ownerEntity.getUser().getPhone());
		
	
		TransactionWalletResponse wallet=new TransactionWalletResponse();
		wallet.setBalance(ownerEntity.getBalance());
		wallet.setCryptocurrency(ownerEntity.getCryptocurrency());
		wallet.setWalletAddress(ownerEntity.getWalletAddress());
		wallet.setUser(user);
		
	
	TransactionResponse transactionResponse =new TransactionResponse();
	
	transactionResponse.setAmount(transaction.getAmount());
	transactionResponse.setTransactionId(transaction.getTransactionId());
	transactionResponse.setType(transaction.getType());
	transactionResponse.setWallet(wallet);
		
	return transactionResponse;
		
	}

}
