package com.konstantin.crypto_wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konstantin.crypto_wallet.dto.auth.AuthRequest;
import com.konstantin.crypto_wallet.model.User;
import com.konstantin.crypto_wallet.repository.UserRepository;
import com.konstantin.crypto_wallet.util.JWTUtils;
import com.konstantin.crypto_wallet.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthControllerTest {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    private User testUser;
    private String passwordInput;

    @BeforeEach
    public void setUp() {
        testUtils.generateData();
        passwordInput = testUtils.getPasswordInput();
        testUser = testUtils.getTestUser();
        userRepository.save(testUser);
    }

    @AfterEach
    public void clean() {
        userRepository.deleteById(testUser.getId());
    }

    @Test
    public void testLogin() throws Exception {
        var authRequest = new AuthRequest(testUser.getUsername(), passwordInput);
        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(authRequest));
        var result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertThat(result.getResponse().getContentAsString()).isNotEmpty();
    }

    @Test
    public void testLoginFail() throws Exception {
        var authRequest = new AuthRequest(testUser.getUsername(), "incorrect_password");
        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(authRequest));
        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogout() throws Exception {
        var token = jwtUtils.generateToken(testUser.getEmail());
        var getRequest = get("/api/profile")
                .header("Authorization", "Bearer " + token);
        mockMvc.perform(getRequest).andExpect(status().isOk());

        var logoutRequest = post("/api/logout")
                .header("Authorization", "Bearer " + token);
        mockMvc.perform(logoutRequest).andExpect(status().isOk());
        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }
}
