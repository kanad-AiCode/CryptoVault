package com.kanad.cryptovault.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.kanad.cryptovault.entity.BlockchainTransactionStatus;
import com.kanad.cryptovault.exception.AmountLessThanZeroException;
import com.kanad.cryptovault.exception.BlockchainPaymentException;
import com.kanad.cryptovault.exception.CryptocurrencyNullException;
import com.kanad.cryptovault.exception.ReceiverAddressEmptyException;

import tools.jackson.databind.JsonNode;

@Service
@Validated
public class BlockchainServiceImpl implements BlockchainService {

    private final BitcoinRpcClient bitcoinRpcClient;

    private final String walletName;

    private final BigDecimal maxTestAmount;

    private final BigDecimal feeRateSatVb;

    public BlockchainServiceImpl(
            BitcoinRpcClient bitcoinRpcClient,
            @Value("${bitcoin.rpc.wallet}") String walletName,
            @Value("${bitcoin.rpc.max-test-amount}") BigDecimal maxTestAmount,
            @Value("${bitcoin.rpc.fee-rate-sat-vb}") BigDecimal feeRateSatVb) {

        this.bitcoinRpcClient = bitcoinRpcClient;
        this.walletName = walletName;
        this.maxTestAmount = maxTestAmount;
        this.feeRateSatVb = feeRateSatVb;
    }

    @Override
    public String sendPayment(
            String cryptocurrency,
            BigDecimal amount,
            String receiverAddress) {

        // 1. Validate cryptocurrency
        if (cryptocurrency == null || cryptocurrency.isBlank()) {
            throw new CryptocurrencyNullException(
                    "Cryptocurrency cannot be null or empty");
        }

        if (!"Bitcoin".equalsIgnoreCase(cryptocurrency.trim())
                && !"BTC".equalsIgnoreCase(cryptocurrency.trim())) {

            throw new BlockchainPaymentException(
                    "The current real blockchain integration supports Bitcoin only");
        }

        // 2. Validate amount
        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new AmountLessThanZeroException(
                    "Amount must be greater than zero");
        }

        // Bitcoin amounts must not have more than 8 decimal places.
        if (amount.stripTrailingZeros().scale() > 8) {
            throw new BlockchainPaymentException(
                    "BTC amounts cannot have more than 8 decimal places");
        }

        // 3. Apply our temporary Testnet4 safety limit
        if (amount.compareTo(maxTestAmount) > 0) {
            throw new BlockchainPaymentException(
                    "Testnet4 test limit exceeded. Maximum amount is "
                            + maxTestAmount + " BTC");
        }

        // 4. Validate receiver address
        if (receiverAddress == null || receiverAddress.isBlank()) {
            throw new ReceiverAddressEmptyException(
                    "Receiver address is required");
        }

        // 5. Submit a real Bitcoin transaction.
        // Bitcoin Core constructs, signs, and broadcasts the transaction.
        JsonNode result = bitcoinRpcClient.callWallet(
                walletName,
                "sendtoaddress",
                receiverAddress,
                amount,
                "CryptoVault external payment",
                "",
                false,
                true,
                null,
                "unset",
                null,
                feeRateSatVb,
                true
        );

        // 6. Extract the real TXID returned by Bitcoin Core
        if (result == null
                || result.get("txid") == null
                || result.get("txid").isNull()
                || result.get("txid").asText().isBlank()) {

            throw new RuntimeException(
                    "Bitcoin Core did not return a transaction ID. "
                            + "Check the transaction state before retrying.");
        }

        return result.get("txid").asText();
    }

    @Override
    public BlockchainTransactionStatus getTransactionStatus(
            String transactionHash) {

        if (transactionHash == null || transactionHash.isBlank()) {
            throw new BlockchainPaymentException(
                    "Transaction hash is required");
        }

        JsonNode transaction;

        // 1. Query the sender wallet for this transaction.
        try {
            transaction = bitcoinRpcClient.callWallet(
                    walletName,
                    "gettransaction",
                    transactionHash
            );
        } catch (RuntimeException e) {

            // A wallet that doesn't know this TXID is not proof
            // that the transaction definitively failed.
            if (e.getMessage() != null
                    && e.getMessage().contains(
                            "Invalid or non-wallet transaction id")) {

                return BlockchainTransactionStatus.NOT_FOUND;
            }

            throw e;
        }

        // 2. Read the confirmation count
        int confirmations =
                transaction.path("confirmations").asInt(0);

        if (confirmations < 0) {
            // Bitcoin Core uses negative confirmations for conflicted
            // transactions.
            return BlockchainTransactionStatus.FAILED;
        }

        if (confirmations > 0) {
            return BlockchainTransactionStatus.CONFIRMED;
        }

        // 3. A wallet transaction with zero confirmations might
        // still be in the node's mempool. Check before calling it pending.
        try {
            bitcoinRpcClient.call(
                    "getmempoolentry",
                    transactionHash
            );

            return BlockchainTransactionStatus.PENDING;

        } catch (RuntimeException e) {

            if (e.getMessage() != null
                    && e.getMessage().contains("Transaction not in mempool")) {

                return BlockchainTransactionStatus.NOT_FOUND;
            }

            // Do not turn network or authentication problems into
            // a false FAILED or NOT_FOUND result.
            throw e;
        }
    }
}
