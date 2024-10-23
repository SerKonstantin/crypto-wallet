package com.konstantin.crypto_wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konstantin.crypto_wallet.dto.transaction.TransactionRequestDTO;
import com.konstantin.crypto_wallet.dto.transaction.TransactionResponseDTO;
import com.konstantin.crypto_wallet.model.transaction.TransactionStatus;
import com.konstantin.crypto_wallet.model.transaction.TransactionType;
import com.konstantin.crypto_wallet.repository.TransactionRepository;
import com.konstantin.crypto_wallet.util.PredefinedTestDataInitializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PredefinedTestDataInitializer predefinedTestDataInitializer;

    @Autowired
    private Web3j web3j;

    @Autowired
    private TransactionRepository transactionRepository;

    @AfterAll
    public static void clean(@Autowired PredefinedTestDataInitializer predefinedTestDataInitializer) {
        predefinedTestDataInitializer.cleanRelatedRepositories();
    }

    // Test full cycle for both sending and receiving a transaction
    @Test
    public void testProcessTransaction() throws Exception {
        var testData = predefinedTestDataInitializer.initializeData();

        // Send a transaction
        var gasPrice = web3j.ethGasPrice().send().getGasPrice().multiply(BigInteger.TWO); // To speed up transaction

        var requestDTO = new TransactionRequestDTO();
        requestDTO.setFromAddress(testData.getWallet().getAddress());
        requestDTO.setToAddress(testData.getReceiverWallet().getAddress());
        requestDTO.setAmount(BigInteger.valueOf(10_000_000_000_000L));
        requestDTO.setGasPrice(gasPrice);
        requestDTO.setGasLimit(BigInteger.valueOf(21_000));
        requestDTO.setFee(requestDTO.getGasPrice().multiply(requestDTO.getGasLimit()));
        requestDTO.setTotal(requestDTO.getAmount().add(requestDTO.getFee()));
        requestDTO.setPrivateKey(testData.getPrivateKey());

        var sendingRequest = post("/api/wallets/{slug}/transactions", testData.getWallet().getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(requestDTO))
                .with(testData.getToken());

        var result = mockMvc.perform(sendingRequest).andExpect(status().isOk()).andReturn();
        var response = om.readValue(result.getResponse().getContentAsString(), TransactionResponseDTO.class);
        var transactionId = response.getId();
        var transactionHash = response.getTransactionHash();

        // Second attempt to make same transaction should redirect to transaction page with pending status
        mockMvc.perform(sendingRequest).andExpect(status().isTemporaryRedirect());

        // Check list of transactions in history for the sender wallet
        mockMvc.perform(get("/api/wallets/{slug}/transactions", testData.getWallet().getSlug())
                        .with(testData.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].transactionHash").exists());

        // Check global transaction history for user
        mockMvc.perform(get("/api/transactions")
                        .with(testData.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].transactionHash").exists());

        // Check individual transaction for the sender wallet
        var showTransactionRequest = get("/api/transactions/{transactionId}", transactionId)
                .with(testData.getToken());
        mockMvc.perform(showTransactionRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromAddress").value(requestDTO.getFromAddress()))
                .andExpect(jsonPath("$.toAddress").value(requestDTO.getToAddress()))
                .andExpect(jsonPath("$.type").value(TransactionType.SEND.toString()))
                .andExpect(jsonPath("$.amount").value(requestDTO.getAmount()))
                .andExpect(jsonPath("$.fee").value(requestDTO.getFee()))
                .andExpect(jsonPath("$.total").value(requestDTO.getTotal()))
                .andExpect(jsonPath("$.transactionHash").exists())
                .andExpect(jsonPath("$.status").exists());

        // Polling for the incoming transaction and transaction status change
        boolean transactionFound = false;
        boolean transactionSuccessful = false;
        long receivedTransactionId = 0;
        for (int i = 0; i < 12; i++) {
            System.out.println("\nAttempt to poll a transaction. Maximum " + ((12 - i) * 10) + " seconds remaining\n");
            var receivedTransaction = transactionRepository
                    .findByTransactionHashAndType(transactionHash, TransactionType.RECEIVE)
                    .orElse(null);
            if (receivedTransaction != null) {
                transactionFound = true;
                receivedTransactionId = receivedTransaction.getId();
                if (receivedTransaction.getStatus().equals(TransactionStatus.COMPLETED)) {
                    transactionSuccessful = true;
                    break;
                }
            }
            Thread.sleep(10000);
        }

        if (!transactionFound) {
            Assertions.fail("Transaction wasn't fetched from blocks");
            return;
        } else if (!transactionSuccessful) {
            Assertions.fail("Transaction was fetched from the block, but didn't change status to COMPLETED");
            return;
        }

        // Check individual transaction for the receiver wallet
        var showReceivedTransactionRequest = get("/api/transactions/{transactionId}", receivedTransactionId)
                .with(testData.getToken());
        mockMvc.perform(showReceivedTransactionRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromAddress").value(requestDTO.getFromAddress()))
                .andExpect(jsonPath("$.toAddress").value(requestDTO.getToAddress()))
                .andExpect(jsonPath("$.type").value(TransactionType.RECEIVE.toString()))
                .andExpect(jsonPath("$.total").value(requestDTO.getAmount()))
                .andExpect(jsonPath("$.transactionHash").value(transactionHash))
                .andExpect(jsonPath("$.status").value(TransactionStatus.COMPLETED.toString()));
    }
}
