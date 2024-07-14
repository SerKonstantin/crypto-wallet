package com.konstantin.crypto_wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konstantin.crypto_wallet.dto.transaction.TransactionRequestDTO;
import com.konstantin.crypto_wallet.model.transaction.TransactionStatus;
import com.konstantin.crypto_wallet.model.transaction.TransactionType;
import com.konstantin.crypto_wallet.util.PredefinedTestDataInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    @AfterEach
    public void clean() {
        predefinedTestDataInitializer.cleanRelatedRepositories();
    }

    @Test
    public void testProcessTransaction() throws Exception {
        var testData = predefinedTestDataInitializer.initializeData();

        var requestDTO = new TransactionRequestDTO();
        requestDTO.setFromAddress(testData.getWallet().getAddress());
        requestDTO.setToAddress("0xAb14868d1Abd7dE5810E70Ed3029239A09625d08"); // TODO change address for receiving test
        requestDTO.setAmount(BigInteger.valueOf(10_000_000_000_000L));
        requestDTO.setGasPrice(BigInteger.valueOf(6_000_000_000L));
        requestDTO.setGasLimit(BigInteger.valueOf(21_000));
        requestDTO.setFee(requestDTO.getGasPrice().multiply(requestDTO.getGasLimit()));
        requestDTO.setTotal(requestDTO.getAmount().add(requestDTO.getFee()));
        requestDTO.setPrivateKey(testData.getPrivateKey());

        var request = post("/api/wallets/{slug}/transactions", testData.getWallet().getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(requestDTO))
                .with(testData.getToken());

        mockMvc.perform(request).andExpect(status().isOk());

        // Second attempt to make same transaction should redirect to transaction page with pending status
        mockMvc.perform(request).andExpect(status().isTemporaryRedirect());

        mockMvc.perform(get("/api/wallets/{slug}/transactions", testData.getWallet().getSlug())
                .with(testData.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fromAddress").value(requestDTO.getFromAddress()))
                .andExpect(jsonPath("$[0].toAddress").value(requestDTO.getToAddress()))
                .andExpect(jsonPath("$[0].type").value(TransactionType.SEND.toString()))
                .andExpect(jsonPath("$[0].amount").value(requestDTO.getAmount()))
                .andExpect(jsonPath("$[0].fee").value(requestDTO.getFee()))
                .andExpect(jsonPath("$[0].total").value(requestDTO.getTotal()))
                .andExpect(jsonPath("$[0].transactionHash").exists())
                .andExpect(jsonPath("$[0].status").value(TransactionStatus.PENDING.toString()));
    }

}
