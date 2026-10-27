package com.kanad.cryptovault.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Component
public class BitcoinRpcClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private final String rpcUrl;
    private final Path cookiePath;

    public BitcoinRpcClient(
            ObjectMapper objectMapper,
            @Value("${bitcoin.rpc.url}") String rpcUrl,
            @Value("${bitcoin.rpc.cookie-path}") String cookiePath) {

        this.objectMapper = objectMapper;
        this.rpcUrl = rpcUrl;
        this.cookiePath = Path.of(cookiePath);

        this.httpClient = HttpClient.newHttpClient();
    }

    // -------------------------------
    // Node-level RPC
    // -------------------------------
    public JsonNode call(String method, Object... params) {

        return callInternal(null, method, params);
    }

    // -------------------------------
    // Wallet-level RPC
    // -------------------------------
    public JsonNode callWallet(
            String walletName,
            String method,
            Object... params) {

        return callInternal(walletName, method, params);
    }

    // -------------------------------
    // Common RPC method
    // -------------------------------
    private JsonNode callInternal(
            String walletName,
            String method,
            Object... params) {

        try {

            // 1. Read Bitcoin Core authentication cookie
            String cookie = Files.readString(cookiePath).trim();

            // 2. Encode cookie for HTTP Basic Authentication
            String encodedCredentials =
                    Base64.getEncoder()
                            .encodeToString(
                                    cookie.getBytes(StandardCharsets.UTF_8));

            // 3. Create JSON-RPC request
            ObjectNode request =
                    objectMapper.createObjectNode();

            request.put("jsonrpc", "2.0");
            request.put("id", "cryptovault");
            request.put("method", method);

            request.set(
                    "params",
                    objectMapper.valueToTree(params));

            // 4. Convert request to JSON
            String requestBody =
                    objectMapper.writeValueAsString(request);

            // 5. Decide the RPC URL
            String targetUrl = rpcUrl;

            if (walletName != null && !walletName.isBlank()) {

                targetUrl =
                        rpcUrl + "/wallet/" + walletName;
            }

            // 6. Build HTTP request
            HttpRequest httpRequest =
                    HttpRequest.newBuilder()
                            .uri(URI.create(targetUrl))
                            .header(
                                    "Authorization",
                                    "Basic " + encodedCredentials)
                            .header(
                                    "Content-Type",
                                    "application/json")
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(requestBody))
                            .build();

            // 7. Send request to Bitcoin Core
            HttpResponse<String> response =
                    httpClient.send(
                            httpRequest,
                            HttpResponse.BodyHandlers.ofString());

            // 8. Parse JSON response
            JsonNode responseJson =
                    objectMapper.readTree(response.body());

            // 9. Check RPC error
            JsonNode error =
                    responseJson.get("error");

            if (error != null && !error.isNull()) {

                throw new RuntimeException(
                        "Bitcoin RPC error: " + error);
            }

            // 10. Return RPC result
            return responseJson.get("result");

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to communicate with Bitcoin Core",
                    e);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Bitcoin RPC request was interrupted",
                    e);
        }
    }
}