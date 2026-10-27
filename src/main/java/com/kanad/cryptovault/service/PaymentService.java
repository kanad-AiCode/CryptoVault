package com.kanad.cryptovault.service;

import java.math.BigDecimal;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kanad.cryptovault.event.ExternalPaymentCreatedEvent;

import com.kanad.cryptovault.entity.PaymentEntity;
import com.kanad.cryptovault.entity.PaymentStatus;
import com.kanad.cryptovault.entity.PaymentType;
import com.kanad.cryptovault.entity.TransactionEntity;
import com.kanad.cryptovault.entity.TransactionSource;
import com.kanad.cryptovault.entity.TransactionType;
import com.kanad.cryptovault.entity.WalletEntity;
import com.kanad.cryptovault.exception.AvailableBalanceLessThanAmountException;
import com.kanad.cryptovault.exception.DifferentCryptocurrencyException;
import com.kanad.cryptovault.exception.InsufficientBalanceException;
import com.kanad.cryptovault.exception.PaymentNotFoundException;
import com.kanad.cryptovault.exception.PaymentStatusException;
import com.kanad.cryptovault.exception.PaymentTypeException;
import com.kanad.cryptovault.exception.PaymentsNotAvailableException;
import com.kanad.cryptovault.exception.SameWalletException;
import com.kanad.cryptovault.exception.WalletNotFoundByAddressException;
import com.kanad.cryptovault.exception.WalletNotFoundByIdException;
import com.kanad.cryptovault.repository.PaymentRepository;
import com.kanad.cryptovault.repository.TransactionRepository;
import com.kanad.cryptovault.repository.WalletRepository;
import com.kanad.cryptovault.request.ExternalPaymentRequest;
import com.kanad.cryptovault.request.PaymentRequest;
import com.kanad.cryptovault.response.PaymentResponse;

@Service
public class PaymentService {
	
	
	
@Autowired
private PaymentRepository repository;

@Autowired
private WalletRepository walletRepository;

@Autowired 
private TransactionRepository transactionRepository;

@Autowired
private BlockchainService blockchainService;

@Autowired
private ApplicationEventPublisher eventPublisher;

@Transactional
public PaymentResponse transferCrypto(PaymentRequest request) {
	
	Optional<PaymentEntity> existingPayment =
	        repository.findByIdempotencyKey(request.getIdempotencyKey());

	if (existingPayment.isPresent()) {
	    return convertToResponse(existingPayment.get());
	}
	
	
	
	Long senderWalletId = request.getSenderWalletId();
    Long receiverWalletId = request.getReceiverWalletId();

    // Same wallet check first
    if (senderWalletId.equals(receiverWalletId)) {
        throw new SameWalletException(
                "Sender and Receiver cannot have same wallet");
    }

    // Determine locking order
    Long smallerWalletId = Math.min(senderWalletId, receiverWalletId);
    Long largerWalletId = Math.max(senderWalletId, receiverWalletId);

    // Lock smaller wallet first
    WalletEntity firstWallet = walletRepository.findByWalletIdForUpdate(smallerWalletId)
            .orElseThrow(() -> new WalletNotFoundByIdException(
                    "Wallet not found with ID: " + smallerWalletId));

    // Lock larger wallet second
    WalletEntity secondWallet = walletRepository.findByWalletIdForUpdate(largerWalletId)
            .orElseThrow(() -> new WalletNotFoundByIdException(
                    "Wallet not found with ID: " + largerWalletId));

    
    // Now restore the actual business roles
    WalletEntity senderWallet;
    WalletEntity receiverWallet;

    if (senderWalletId.equals(smallerWalletId)) {
        senderWallet = firstWallet;
        receiverWallet = secondWallet;
    } else {
        senderWallet = secondWallet;
        receiverWallet = firstWallet;
    }

	
	
	if(!senderWallet.getCryptocurrency().equals( receiverWallet.getCryptocurrency()) ||
			!senderWallet.getCryptocurrency().equals(request.getCryptocurrency()) ) {
		
		throw new DifferentCryptocurrencyException("Crypto coin is not matching");
	}
	
	if(senderWallet.getBalance().compareTo(request.getAmount())<0) {
		
		throw new InsufficientBalanceException("Sender doesn't have sufficient balance");
	}
	
	BigDecimal senderBalance=senderWallet.getBalance().subtract(request.getAmount());
	
	BigDecimal receiverBalance= receiverWallet.getBalance().add(request.getAmount());
	
	senderWallet.setBalance(senderBalance);
	 receiverWallet.setBalance(receiverBalance);
	
	TransactionEntity senderTransaction=new TransactionEntity();
	
	senderTransaction.setAmount( request.getAmount());
	senderTransaction.setType(TransactionType.WITHDRAW);
	senderTransaction.setSource(TransactionSource.INTERNAL_TRANSFER);
	senderTransaction.setWallet(senderWallet);
	
	TransactionEntity recieverTransaction=new TransactionEntity();
	
	recieverTransaction.setAmount(request.getAmount());
	recieverTransaction.setType(TransactionType.DEPOSIT);
	recieverTransaction.setSource(TransactionSource.INTERNAL_TRANSFER);
	recieverTransaction.setWallet( receiverWallet);
	
	walletRepository.save(senderWallet);
	walletRepository.save( receiverWallet);
	transactionRepository.save(senderTransaction);
	transactionRepository.save(recieverTransaction);
	
	PaymentEntity payment=new PaymentEntity();
	
	
	payment.setStatus(PaymentStatus.PROCESSING);
	payment.setAmount(request.getAmount());
	payment.setCryptocurrency(request.getCryptocurrency());
	payment.setSenderwallet(senderWallet);
	payment.setReceiverWallet( receiverWallet);
	payment.setReference(UUID.randomUUID().toString());
	payment.setIdempotencyKey(request.getIdempotencyKey());
	payment.setPaymentType(PaymentType.INTERNAL);
	payment.setStatus(PaymentStatus.COMPLETED);
	
	PaymentEntity savedPayment = repository.save(payment);
	
	
    PaymentResponse response=new PaymentResponse();
	
	response.setAmount(request.getAmount());
	response.setCryptoCurrency(savedPayment .getCryptocurrency());
	response.setCreatedAt(savedPayment .getCreatedAt());
	response.setPaymentId(savedPayment .getPaymentId());
	response.setSenderWalletId(senderWallet.getWalletId());
	response.setReceiverWalletId( receiverWallet.getWalletId());
	response.setReference(savedPayment .getReference());
	response.setStatus(savedPayment .getStatus());
	response.setIdempotencyKey(savedPayment.getIdempotencyKey());
    response.setTransactionHash(savedPayment.getTransactionHash());
   response.setPaymentType(savedPayment.getPaymentType());
	
	return response;
	
	


}
@Transactional
public PaymentResponse createExternalPayment(ExternalPaymentRequest request) {

    // 1. Idempotency check
    Optional<PaymentEntity> existingPayment =
            repository.findByIdempotencyKey(request.getIdempotencyKey());

    if (existingPayment.isPresent()) {
        return convertToResponse(existingPayment.get());
    }

    // 2. Find sender wallet
    WalletEntity senderWallet =
            walletRepository.findByWalletId(request.getSenderWalletId())
                    .orElseThrow(() ->
                            new WalletNotFoundByIdException(
                                    "Wallet not found with ID: "
                                            + request.getSenderWalletId()));

    // 3. Cryptocurrency validation
    if (!senderWallet.getCryptocurrency()
            .equals(request.getCryptocurrency())) {

        throw new DifferentCryptocurrencyException(
                "Crypto coin is not matching");
    }

    // 4. Available balance validation
    if (senderWallet.getAvailableBalance()
            .compareTo(request.getAmount()) < 0) {

        throw new AvailableBalanceLessThanAmountException(
                "Requested amount cannot be greater than available balance");
    }

    // 5. Reserve funds
    BigDecimal newLockedBalance =
            senderWallet.getLockedBalance()
                    .add(request.getAmount());

    senderWallet.setLockedBalance(newLockedBalance);

    walletRepository.save(senderWallet);

    // 6. Create PENDING external payment
    PaymentEntity payment = new PaymentEntity();

    payment.setAmount(request.getAmount());
    payment.setCryptocurrency(request.getCryptocurrency());
    payment.setExternalReceiverAddress(
            request.getExternalReceiverAddress());
    payment.setIdempotencyKey(request.getIdempotencyKey());
    payment.setPaymentType(PaymentType.EXTERNAL);
    payment.setReference(UUID.randomUUID().toString());
    payment.setStatus(PaymentStatus.PENDING);
    payment.setSenderwallet(senderWallet);

    // 7. Save payment
    PaymentEntity savedPayment = repository.save(payment);

    // 8. Publish event	
    eventPublisher.publishEvent(
            new ExternalPaymentCreatedEvent(
                    savedPayment.getPaymentId()));

    // 9. Return PENDING response
    return convertToResponse(savedPayment);
}




public PaymentResponse getPaymentById(long paymentId) {
	
	
	PaymentEntity payment=repository.findById(paymentId).orElseThrow(() -> new PaymentNotFoundException("Payment with ID " + paymentId + " not found"));
	
	return convertToResponse(payment);
}


public List<PaymentResponse> getAllPayment(){
	
	List<PaymentEntity> allPayments=repository.findAll();
	
	List<PaymentResponse> allResponse=new ArrayList<>();
	
	for(PaymentEntity payment:allPayments) {

		allResponse.add(convertToResponse(payment));
	}
	
	return allResponse;
}

public List<PaymentResponse> getPaymentsByWalletId(Long walletId){
	
	WalletEntity wallet=walletRepository.findById(walletId).
			orElseThrow(()->  new WalletNotFoundByIdException("Wallet not found by ID:" + walletId ));
	
	List<PaymentEntity> paymentsEntity=repository.findBySenderWallet_WalletIdOrReceiverWallet_WalletId(walletId, walletId);
	
  if(paymentsEntity.isEmpty()) {
		
		throw new PaymentsNotAvailableException("No recent payments with wallet ID :" + walletId);
	}

	List<PaymentResponse> allPayments=new ArrayList<>();
	
	for(PaymentEntity payment:paymentsEntity) {
		
		allPayments.add(convertToResponse(payment));
	}
	
	
	
	return allPayments;
	
}



public PaymentResponse convertToResponse(PaymentEntity entity) {
	
	 PaymentResponse response = new PaymentResponse();

	    response.setPaymentId(entity.getPaymentId());

	    // Sender wallet exists for both internal and external payments
	    if (entity.getSenderwallet() != null) {
	        response.setSenderWalletId(
	                entity.getSenderwallet().getWalletId());
	    }

	    // Receiver wallet exists only for INTERNAL payments
	    if (entity.getReceiverWallet() != null) {
	        response.setReceiverWalletId(
	                entity.getReceiverWallet().getWalletId());
	    }

	    response.setAmount(entity.getAmount());
	    response.setCryptoCurrency(entity.getCryptocurrency());
	    response.setStatus(entity.getStatus());
	    response.setReference(entity.getReference());
	    response.setCreatedAt(entity.getCreatedAt());
	    response.setIdempotencyKey(entity.getIdempotencyKey());

	    // EXTERNAL payment information
	    response.setPaymentType(entity.getPaymentType());

	    response.setExternalReceiverAddress(
	            entity.getExternalReceiverAddress());

	    response.setTransactionHash(
	            entity.getTransactionHash());

	    return response;

}



}
