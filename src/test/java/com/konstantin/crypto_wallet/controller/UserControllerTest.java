package com.konstantin.crypto_wallet.controller;

import static org.assertj.core.api.Assertions.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konstantin.crypto_wallet.dto.user.UserRegistrationDTO;
import com.konstantin.crypto_wallet.dto.user.UserUpdateDTO;
import com.konstantin.crypto_wallet.repository.UserRepository;
import com.konstantin.crypto_wallet.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TestUtils testUtils;

    @AfterEach
    public void clean() {
        testUtils.cleanAllRepositories();
    }

    @Test
    public void testRegister() throws Exception {
        var userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setNickname("TestUser");
        userRegistrationDTO.setEmail("testuser@gmail.com");
        userRegistrationDTO.setPassword("qwertyuiop");

        var request = post("/api/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userRegistrationDTO));
        mockMvc.perform(request).andExpect(status().isCreated());

        var user = userRepository.findByEmail(userRegistrationDTO.getEmail()).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isPositive();
        assertThat(user.getNickname()).isEqualTo(userRegistrationDTO.getNickname());
        assertThat(user.getPassword()).isNotEmpty().isNotEqualTo(userRegistrationDTO.getPassword());
        assertThat(user.getCreatedAt()).isEqualTo(user.getUpdatedAt())
                .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.MINUTES));

        mockMvc.perform(request).andExpect(status().isConflict()); // Cannot create user with same data
    }

    @ParameterizedTest
    @MethodSource("supplyRegisterAndUpdateWithInvalidData")
    public void testRegisterFails(String nickname, String email, String password) throws Exception {
        var userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setNickname(nickname);
        userRegistrationDTO.setEmail(email);
        userRegistrationDTO.setPassword(password);

        var request = post("/api/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userRegistrationDTO));
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    private Stream<Arguments> supplyRegisterAndUpdateWithInvalidData() {
        var nickname = "TestUser";
        var email = "testuser@gmail.com";
        var password = "qwertyuiop";
        return Stream.of(
                Arguments.of("!@#$%^&*()[]{}", email, password),
                Arguments.of("", email, password),
                Arguments.of(null, email, password),
                Arguments.of(nickname, "not_email", password),
                Arguments.of(nickname, "", password),
                Arguments.of(nickname, null, password),
                Arguments.of(nickname, email, "short"),
                Arguments.of(nickname, email, ""),
                Arguments.of(nickname, email, null)
        );
    }

    @Test
    public void testShowProfile() throws Exception {
        var testData = testUtils.generateData();
        var user = testData.getUser();

        var request = get("/api/profile");
        mockMvc.perform(request).andExpect(status().isUnauthorized());
        mockMvc.perform(request.with(testData.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(user.getNickname()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.role").value(user.getRole().name()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void testUpdateProfile() throws Exception {
        var testData = testUtils.generateData();

        var userUpdateDto = new UserUpdateDTO();
        userUpdateDto.setNickname(JsonNullable.of("NewNickname"));
        userUpdateDto.setEmail(JsonNullable.of("newmail@gmail.com"));
        userUpdateDto.setPassword(JsonNullable.of("asdfghjkl"));

        var request = put("/api/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userUpdateDto));
        mockMvc.perform(request).andExpect(status().isUnauthorized());
        mockMvc.perform(request.with(testData.getToken())).andExpect(status().isOk());

        var updatedUser = userRepository.findById(testData.getUser().getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getNickname()).isEqualTo("NewNickname");
        assertThat(updatedUser.getEmail()).isEqualTo("newmail@gmail.com");
        assertThat(updatedUser.getPassword()).isNotEmpty()
                .isNotEqualTo(testData.getUser().getPassword())
                .isNotEqualTo(testData.getPasswordInput())
                .isNotEqualTo("asdfghjkl");
    }

    @Test
    public void testPartialUpdateUser() throws Exception {
        var testData = testUtils.generateData();

        var userUpdateDto = new UserUpdateDTO();
        userUpdateDto.setNickname(JsonNullable.of("NewNickname"));

        var request = put("/api/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userUpdateDto));
        mockMvc.perform(request.with(testData.getToken())).andExpect(status().isOk());

        var updatedUser = userRepository.findById(testData.getUser().getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getNickname()).isEqualTo("NewNickname");
        assertThat(updatedUser.getEmail()).isEqualTo(testData.getUser().getEmail());
    }

    @ParameterizedTest
    @MethodSource("supplyRegisterAndUpdateWithInvalidData")
    public void testUpdateFails(String nickname, String email, String password) throws Exception {
        var userUpdateDto = new UserUpdateDTO();
        userUpdateDto.setNickname(JsonNullable.of(nickname));
        userUpdateDto.setEmail(JsonNullable.of(email));
        userUpdateDto.setPassword(JsonNullable.of(password));

        var request = put("/api/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userUpdateDto));
        var token = testUtils.generateData().getToken();
        mockMvc.perform(request.with(token)).andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteUser() throws Exception {
        var testData = testUtils.generateData();
        var userId = testData.getUser().getId();

        assertThat(userRepository.findById(userId)).isPresent();
        var request = delete("/api/profile")
                .param("confirmation", "I want to delete my account permanently");
        mockMvc.perform(request).andExpect(status().isUnauthorized());
        mockMvc.perform(request.with(testData.getToken())).andExpect(status().isNoContent());
        assertThat(userRepository.findById(userId)).isEmpty();
    }
}
