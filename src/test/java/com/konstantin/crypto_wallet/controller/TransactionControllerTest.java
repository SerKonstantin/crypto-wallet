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

        mockMvc.perform(post("/api/wallets/{slug}/transactions", testData.getWallet().getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(requestDTO))
                        .with(testData.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromAddress").value(requestDTO.getFromAddress()))
                .andExpect(jsonPath("$.toAddress").value(requestDTO.getToAddress()))
                .andExpect(jsonPath("$.type").value(TransactionType.SEND.toString()))
                .andExpect(jsonPath("$.amount").value(requestDTO.getAmount()))
                .andExpect(jsonPath("$.fee").value(requestDTO.getFee()))
                .andExpect(jsonPath("$.total").value(requestDTO.getTotal()))
                .andExpect(jsonPath("$.transactionHash").exists())
                .andExpect(jsonPath("$.status").value(TransactionStatus.PENDING.toString()));
    }

}
