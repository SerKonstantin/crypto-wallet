package com.konstantin.crypto_wallet.controller;

import static org.assertj.core.api.Assertions.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konstantin.crypto_wallet.dto.wallet.WalletCreateDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletUpdateDTO;
import com.konstantin.crypto_wallet.exception.ResourceNotFoundException;
import com.konstantin.crypto_wallet.model.User;
import com.konstantin.crypto_wallet.model.Wallet;
import com.konstantin.crypto_wallet.repository.UserRepository;
import com.konstantin.crypto_wallet.repository.WalletRepository;
import com.konstantin.crypto_wallet.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TestUtils testUtils;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor otherUserToken;
    private User testUser;
    private User otherUser;
    private Wallet testWallet;

    @BeforeEach
    public void setUp() {
        testUtils.generateData();
        testUser = testUtils.getTestUser();
        testWallet = testUtils.getTestWallet();
        userRepository.save(testUser);
        walletRepository.save(testWallet);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testUtils.generateData();
        otherUser = testUtils.getTestUser();
        userRepository.save(otherUser);
        otherUserToken = jwt().jwt(builder -> builder.subject(otherUser.getEmail()));
    }

    @AfterEach
    public void clean() {
        walletRepository.deleteById(testWallet.getId());
        userRepository.deleteById(testUser.getId());
        userRepository.deleteById(otherUser.getId());
    }

    @Test
    public void testCreateWallet() throws Exception {
        var walletCreateDTO = new WalletCreateDTO();
        walletCreateDTO.setAddress("0xb794f5ea0ba39494ce839613fffba74279579268");
        walletCreateDTO.setName("Henkilökohtainen");

        var request = post("/api/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(walletCreateDTO))
                .with(token);
        mockMvc.perform(request).andExpect(status().isCreated());

        var wallets = walletRepository.findByUserId(testUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallets not found"));
        assertThat(wallets).hasSize(2);
        var wallet = wallets.get(1);
        assertThat(wallet.getAddress()).isEqualTo(walletCreateDTO.getAddress());
        assertThat(wallet.getName()).isEqualTo("Henkilökohtainen");
        assertThat(wallet.getSlug()).isEqualTo("henkilokohtainen");

        mockMvc.perform(request).andExpect(status().isConflict()); // Cannot create wallet with same name
    }

    @ParameterizedTest
    @MethodSource("supplyInvalidWalletData")
    public void testCreateWalletFails(String name, String address) throws Exception {
        var walletCreateDTO = new WalletCreateDTO();
        walletCreateDTO.setName(name);
        walletCreateDTO.setAddress(address);

        var request = post("/api/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(walletCreateDTO))
                .with(token);
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    private Stream<Arguments> supplyInvalidWalletData() {
        return Stream.of(
                Arguments.of("", "address"),
                Arguments.of(null, "address"),
                Arguments.of("name", ""),
                Arguments.of("name", null)
        );
    }

    @Test
    public void testShowWallets() throws Exception {
        var request = get("/api/wallets");
        mockMvc.perform(request).andExpect(status().isUnauthorized());
        mockMvc.perform(request.with(token)).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value(testWallet.getName()))
                .andExpect(jsonPath("$[0].address").value(testWallet.getAddress()));
    }

    @Test
    public void testShowWallet() throws Exception {
        var request = get("/api/wallets/" + testWallet.getSlug());
        mockMvc.perform(request).andExpect(status().isUnauthorized());
        mockMvc.perform(request.with(otherUserToken)).andExpect(status().isNotFound());
        mockMvc.perform(request.with(token)).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testWallet.getName()))
                .andExpect(jsonPath("$.address").value(testWallet.getAddress()));
    }

    @Test
    public void testUpdateWallet() throws Exception {
        var walletUpdateDTO = new WalletUpdateDTO();
        walletUpdateDTO.setName("New Name");

        var request = put("/api/wallets/" + testWallet.getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(walletUpdateDTO));
        mockMvc.perform(request).andExpect(status().isUnauthorized());
        mockMvc.perform(request.with(otherUserToken)).andExpect(status().isNotFound());
        mockMvc.perform(request.with(token)).andExpect(status().isOk());

        var updatedWallet = walletRepository.findById(testWallet.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        assertThat(updatedWallet.getName()).isEqualTo("New Name");
        assertThat(updatedWallet.getSlug()).isEqualTo("newname");
    }

    @Test
    public void testUpdateWalletFails() throws Exception {
        var walletUpdateDTO = new WalletUpdateDTO();
        walletUpdateDTO.setName("");

        var request = put("/api/wallets/" + testWallet.getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(walletUpdateDTO))
                .with(token);
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteWallet() throws Exception {
        assertThat(walletRepository.findById(testWallet.getId())).isPresent();
        var request = delete("/api/wallets/" + testWallet.getSlug())
                .param("confirmation", "I want to delete this wallet permanently");
        mockMvc.perform(request).andExpect(status().isUnauthorized());
        mockMvc.perform(request.with(otherUserToken)).andExpect(status().isNotFound());
        mockMvc.perform(request.with(token)).andExpect(status().isNoContent());
        assertThat(walletRepository.findById(testWallet.getId())).isEmpty();
    }
}
