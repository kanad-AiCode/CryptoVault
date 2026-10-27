package com.kanad.cryptovault.repository;

import java.util.List;
import java.util.Optional;
import java.util.List;

import com.kanad.cryptovault.entity.PaymentEntity;
import com.kanad.cryptovault.entity.PaymentStatus;
import com.kanad.cryptovault.entity.PaymentType;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanad.cryptovault.entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity,Long> {
	
	List<PaymentEntity> findBySenderWallet_WalletIdOrReceiverWallet_WalletId(Long senderWalletId, Long receiverWalletId);
	
	Optional<PaymentEntity> findByIdempotencyKey(String idempotencyKey);
	
	List<PaymentEntity> findByStatusAndPaymentType(
	        PaymentStatus status,
	        PaymentType paymentType
	);
}
