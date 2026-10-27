package com.kanad.cryptovault.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanad.cryptovault.entity.TransactionEntity;

public interface TransactionRepository extends JpaRepository<TransactionEntity,Long>{
	
	
	List<TransactionEntity> findAll();
	
	List<TransactionEntity> findByWallet_WalletId(Long walletId);
	

}
