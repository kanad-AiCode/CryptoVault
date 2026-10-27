package com.kanad.cryptovault.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kanad.cryptovault.entity.BlockchainTransactionStatus;
import com.kanad.cryptovault.entity.PaymentEntity;
import com.kanad.cryptovault.entity.PaymentStatus;
import com.kanad.cryptovault.entity.PaymentType;
import com.kanad.cryptovault.repository.PaymentRepository;

@Component
public class BlockchainConfirmationScheduler {

    private static final Logger logger =
            LoggerFactory.getLogger(BlockchainConfirmationScheduler.class);

    private final PaymentRepository paymentRepository;

    private final BlockchainConfirmationProcessor confirmationProcessor;

    public BlockchainConfirmationScheduler(
            PaymentRepository paymentRepository,
            BlockchainConfirmationProcessor confirmationProcessor) {

        this.paymentRepository = paymentRepository;
        this.confirmationProcessor = confirmationProcessor;
    }

    @Scheduled(fixedDelayString = "${cryptovault.confirmation.poll-interval-ms:30000}")
    public void checkBroadcastedPayments() {

        List<PaymentEntity> payments =
                paymentRepository.findByStatusAndPaymentType(
                        PaymentStatus.BROADCASTED,
                        PaymentType.EXTERNAL
                );

        for (PaymentEntity payment : payments) {

            Long paymentId = payment.getPaymentId();

            try {

                BlockchainTransactionStatus status =
                        confirmationProcessor.checkPaymentConfirmation(paymentId);

                logger.info(
                        "Confirmation check: paymentId={}, blockchainStatus={}",
                        paymentId,
                        status
                );

            } catch (Exception exception) {

                // One problematic payment must not stop checks for
                // the remaining payments.
                logger.error(
                        "Confirmation check failed for paymentId={}: {}",
                        paymentId,
                        exception.getMessage()
                );
            }
        }
    }
}