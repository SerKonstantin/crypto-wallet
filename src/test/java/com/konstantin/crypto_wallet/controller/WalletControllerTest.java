package com.konstantin.crypto_wallet.controller;

import static org.assertj.core.api.Assertions.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konstantin.crypto_wallet.dto.wallet.WalletCreateDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletImportDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletUpdateDTO;
import com.konstantin.crypto_wallet.repository.UserRepository;
import com.konstantin.crypto_wallet.repository.WalletRepository;
import com.konstantin.crypto_wallet.util.RandomTestDataGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

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
    private RandomTestDataGenerator randomTestDataGenerator;

    @AfterEach
    public void clean() {
        randomTestDataGenerator.cleanAllRepositories();
    }

    @Test
    public void testCreateWallet() throws Exception {
        var testData = randomTestDataGenerator.generateData();

        var walletCreateDTO = new WalletCreateDTO();
        walletCreateDTO.setAddress("0x1234");
        walletCreateDTO.setName("Henkilökohtainen");

        var request = post("/api/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(walletCreateDTO))
                .with(testData.getToken());
        mockMvc.perform(request).andExpect(status().isCreated());

        var wallets = walletRepository.findByUserId(testData.getUser().getId()).orElse(null);
        assertThat(wallets).hasSize(2);
        var wallet = wallets.get(1);
        assertThat(wallet.getAddress()).isEqualTo("0x1234");
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
                .with(randomTestDataGenerator.generateData().getToken());
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> supplyInvalidWalletData() {
        return Stream.of(
                Arguments.of("", "address"),
                Arguments.of(null, "address"),
                Arguments.of("name", ""),
                Arguments.of("name", null)
        );
    }

    @Test
    public void testShowWallets() throws Exception {
        var testData = randomTestDataGenerator.generateData();

        var request = get("/api/wallets");
        mockMvc.perform(request).andExpect(status().isUnauthorized());
        mockMvc.perform(request.with(testData.getToken())).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value(testData.getWallet().getName()))
                .andExpect(jsonPath("$[0].address").value(testData.getWallet().getAddress()));
    }

    @Test
    public void testShowWallet() throws Exception {
        var testData = randomTestDataGenerator.generateData();
        var otherUserData = randomTestDataGenerator.generateData();

        var request = get("/api/wallets/" + testData.getWallet().getSlug());
        mockMvc.perform(request).andExpect(status().isUnauthorized());
        mockMvc.perform(request.with(otherUserData.getToken())).andExpect(status().isNotFound());
        mockMvc.perform(request.with(testData.getToken())).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testData.getWallet().getName()))
                .andExpect(jsonPath("$.address").value(testData.getWallet().getAddress()));
    }

    @Test
    public void testUpdateWallet() throws Exception {
        var testData = randomTestDataGenerator.generateData();
        var otherUserData = randomTestDataGenerator.generateData();

        var walletUpdateDTO = new WalletUpdateDTO();
        walletUpdateDTO.setName("New Name");

        var request = put("/api/wallets/" + testData.getWallet().getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(walletUpdateDTO));
        mockMvc.perform(request).andExpect(status().isUnauthorized());
        mockMvc.perform(request.with(otherUserData.getToken())).andExpect(status().isNotFound());
        mockMvc.perform(request.with(testData.getToken())).andExpect(status().isOk());

        var updatedWallet = walletRepository.findById(testData.getWallet().getId()).orElse(null);
        assertThat(updatedWallet).isNotNull();
        assertThat(updatedWallet.getName()).isEqualTo("New Name");
        assertThat(updatedWallet.getSlug()).isEqualTo("newname");
    }

    @Test
    public void testUpdateWalletFails() throws Exception {
        var testData = randomTestDataGenerator.generateData();

        var walletUpdateDTO = new WalletUpdateDTO();
        walletUpdateDTO.setName("");

        var request = put("/api/wallets/" + testData.getWallet().getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(walletUpdateDTO))
                .with(testData.getToken());
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteWallet() throws Exception {
        var testData = randomTestDataGenerator.generateData();
        var otherUserData = randomTestDataGenerator.generateData();

        assertThat(walletRepository.findById(testData.getWallet().getId())).isPresent();
        var request = delete("/api/wallets/" + testData.getWallet().getSlug())
                .param("confirmation", "I want to delete this wallet permanently");
        mockMvc.perform(request).andExpect(status().isUnauthorized());
        mockMvc.perform(request.with(otherUserData.getToken())).andExpect(status().isNotFound());
        mockMvc.perform(request.with(testData.getToken())).andExpect(status().isNoContent());
        assertThat(walletRepository.findById(testData.getWallet().getId())).isEmpty();
    }

    @Test
    public void testImportWallet() throws Exception {
        var testData = randomTestDataGenerator.generateData();

        var walletImportDTO = new WalletImportDTO();
        walletImportDTO.setPrivateKey("1383ad52dca31407f1955d25c79785ff75249cf975d481c7d3a1c96ea9e638a5");
        walletImportDTO.setName("Imported Wallet");

        var request = post("/api/wallets/import")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(walletImportDTO));
        mockMvc.perform(request).andExpect(status().isUnauthorized());
        mockMvc.perform(request.with(testData.getToken())).andExpect(status().isCreated());

        var wallets = walletRepository.findByUserId(testData.getUser().getId()).orElse(null);
        assertThat(wallets).hasSize(2);
        var wallet = wallets.get(1);
        assertThat(wallet.getAddress()).isEqualTo("0xab14868d1abd7de5810e70ed3029239a09625d08");
        assertThat(wallet.getName()).isEqualTo("Imported Wallet");
        assertThat(wallet.getSlug()).isEqualTo("importedwallet");

        // Cannot create wallet with same address
        mockMvc.perform(request.with(testData.getToken())).andExpect(status().isConflict());
    }

    @Test
    public void testImportWalletWithInvalidPrivateKey() throws Exception {
        var walletImportDTO = new WalletImportDTO();
        walletImportDTO.setPrivateKey("invalidprivatekey");
        walletImportDTO.setName("Invalid Wallet");

        var request = post("/api/wallets/import")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(walletImportDTO))
                .with(randomTestDataGenerator.generateData().getToken());
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void testImportWalletFails() throws Exception {
        var walletImportDTO = new WalletImportDTO();
        walletImportDTO.setPrivateKey(null);
        walletImportDTO.setName("Invalid Wallet");

        var request = post("/api/wallets/import")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(walletImportDTO))
                .with(randomTestDataGenerator.generateData().getToken());
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }
}
