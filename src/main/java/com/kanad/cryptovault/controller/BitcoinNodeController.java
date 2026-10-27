package com.kanad.cryptovault.controller;

import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kanad.cryptovault.request.BitcoinTestSendRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kanad.cryptovault.service.BitcoinRpcClient;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/bitcoin")
public class BitcoinNodeController {

    private final BitcoinRpcClient bitcoinRpcClient;

    public BitcoinNodeController(
            BitcoinRpcClient bitcoinRpcClient) {

        this.bitcoinRpcClient = bitcoinRpcClient;
    }

    
    
    @GetMapping("/node-info")
    public JsonNode getNodeInfo() {

        return bitcoinRpcClient.call(
                "getblockchaininfo");
    }
    
    
    
    @GetMapping("/wallet-info")
    public JsonNode getWalletInfo() {

        return bitcoinRpcClient.call(
                "getwalletinfo"
        );
        
        
        
       
    }
    
    @GetMapping("/utxos")
    public JsonNode getUtxos() {

        return bitcoinRpcClient.callWallet(
                "cryptovault-test",
                "listunspent",
                0,
                9999999
        );
    }
    
    @PostMapping("/test-send")
    public JsonNode sendTestBitcoin(
            @Valid @RequestBody BitcoinTestSendRequest request) {

        return bitcoinRpcClient.callWallet(
        		 "cryptovault-test",
                "sendtoaddress",
                request.getReceiverAddress(),
                request.getAmount(),
                "CryptoVault Java RPC test",
                "",
                false,
                true,
                null,
                "unset",
                null,
                new BigDecimal("2"),
                true
        );
    }
}