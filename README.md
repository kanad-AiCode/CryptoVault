# CryptoVault — Cryptocurrency Wallet & Payment Management System

CryptoVault is a Java/Spring Boot backend and web frontend for managing cryptocurrency wallets and payment workflows. It uses MySQL for application data and integrates with Bitcoin Core on **Bitcoin Testnet4** through JSON-RPC for test transaction broadcasting and transaction-status checks.

> **Development/demo project:** Blockchain operations in this repository are intended for Bitcoin Testnet4 only. Testnet coins have no intended monetary value. This project is not a production-ready or audited cryptocurrency custody/payment system.

## Features

- REST APIs for user, wallet, and payment management.
- Spring Data JPA/Hibernate persistence with MySQL.
- Request validation, DTO-based responses, and exception handling.
- Internal wallet transfers with transactional processing and balance reservation.
- External-payment workflow with idempotency and asynchronous processing.
- Bitcoin Core JSON-RPC integration for Testnet4 transaction broadcasting.
- Persistence of real Bitcoin transaction IDs and transaction-status checks.
- Automatic confirmation polling for broadcasted external payments (if enabled in the local configuration).
- Dark-themed browser UI served by Spring Boot from `src/main/resources/static`.

## Technology stack

- Java 21
- Spring Boot / Spring MVC
- Spring Data JPA and Hibernate
- MySQL
- Maven
- HTML, CSS, JavaScript
- Bitcoin Core JSON-RPC (Testnet4)
- Postman for API testing

## Architecture

```text
Browser (HTML/CSS/JavaScript)
              |
              v
       Spring Boot REST API
          /           \
         v             v
       MySQL      BitcoinRpcClient
                         |
                         v
                    Bitcoin Core
                      Testnet4
```

## Prerequisites

- JDK 21
- Maven (or run through Eclipse as a Spring Boot application)
- MySQL
- Bitcoin Core configured for Testnet4, with its local RPC server enabled and the test wallet created/loaded

## Local setup

1. Clone this repository.
2. Create a MySQL database named `cryptovault_db`.
3. Copy `src/main/resources/application-example.properties` to `src/main/resources/application.properties`.
4. Set your local database credentials in `application.properties` or provide the documented environment variables. **Do not commit this local file.**
5. Configure Bitcoin Core for Testnet4 with local RPC enabled. Confirm that the RPC endpoint and `.cookie` path in `application.properties` match your machine. Keep the RPC interface bound to localhost; do not expose it publicly.
6. Ensure the configured Bitcoin Core wallet exists and is loaded before using wallet RPC endpoints.
7. Start the app from Eclipse, or run:

   ```bash
   mvn spring-boot:run
   ```

8. Open `http://localhost:8080/` to view the frontend.

### Environment variables

The example configuration supports these variables:

- `DB_URL` — defaults to `jdbc:mysql://localhost:3306/cryptovault_db`
- `DB_USERNAME` — defaults to `root`
- `DB_PASSWORD` — set this to your local MySQL password

Bitcoin Core settings and the cookie path may need to be edited for the operating system and data directory in use.

## API endpoints

Base URL for local development: `http://localhost:8080`

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/wallets` | List wallets |
| GET | `/wallets/{walletId}` | Get a wallet by ID |
| GET | `/wallets/user/{userId}` | List a user's wallets |
| POST | `/wallets?userId={id}` | Create a wallet |
| POST | `/payments/internalTransfer` | Create an internal transfer |
| POST | `/payments/externalTransfer` | Create an external payment request |
| GET | `/payments/allPayments` | List payments |
| GET | `/payments/getById?paymentId={id}` | Retrieve payment details/status |
| GET | `/payments/wallet/{walletId}` | List payments for a wallet |
| GET | `/payments/{paymentId}/confirmation` | Manually check blockchain status |
| GET | `/bitcoin/node-info` | Read Bitcoin Core node information |
| GET | `/bitcoin/wallet-info` | Read configured Bitcoin Core wallet information |
| GET | `/bitcoin/utxos` | Inspect wallet UTXOs |

The `/bitcoin/test-send` endpoint is a development/testing endpoint that can broadcast a real Testnet4 transaction. Use it only with testnet addresses and small test amounts.

## Security notes

- Never commit passwords, API tokens, Bitcoin RPC cookies, wallet files, private keys, or database dumps.
- Bitcoin Core RPC should stay on localhost and must not be exposed to the public Internet.
- The current demo has not been audited for production use. Authentication/authorization, robust reconciliation after uncertain broadcasts, high-availability scheduling, reorganization-safe confirmation policies, and financial accounting/reconciliation require further work before production deployment.

## Project status

CryptoVault has demonstrated real transaction broadcasting through Bitcoin Core on Testnet4 and records the returned TXID in the application payment flow. The confirmation flow has been tested against the local Testnet4 node. This repository is intended to demonstrate backend engineering and testnet integration, not production custody of digital assets.
