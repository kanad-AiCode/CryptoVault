package com.kanad.cryptovault.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kanad.cryptovault.entity.WalletEntity;

import jakarta.persistence.LockModeType;

public interface WalletRepository extends JpaRepository<WalletEntity,Long> {
	
	Optional<WalletEntity> findByWalletAddress(String walletAddress);
	
	 List<WalletEntity> findByUser_Id(Long userId);
	
	 boolean existsByWalletAddress(String walletAddress);
	 
	 @Lock(LockModeType.PESSIMISTIC_WRITE)
	 
	 @Query("SELECT w FROM WalletEntity w WHERE w.walletId = :walletId")
	    Optional<WalletEntity> findByWalletIdForUpdate(@Param("walletId") Long walletId);
	 
	 Optional<WalletEntity> findByWalletId(Long walletId);

     List<WalletEntity> findAll();
     
    
     

}
