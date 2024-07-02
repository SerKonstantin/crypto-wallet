package com.konstantin.crypto_wallet.controller;

import static org.assertj.core.api.Assertions.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konstantin.crypto_wallet.dto.user.UserRegistrationDTO;
import com.konstantin.crypto_wallet.dto.user.UserUpdateDTO;
import com.konstantin.crypto_wallet.exception.ResourceNotFoundException;
import com.konstantin.crypto_wallet.model.User;
import com.konstantin.crypto_wallet.repository.UserRepository;
import com.konstantin.crypto_wallet.util.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TestUtils testUtils;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor otherToken;
    private User testUser;
    private User otherUser;

    @BeforeEach
    public void setUp() {
        testUtils.generateData();
        testUser = testUtils.getTestUser();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testUtils.generateData();
        otherUser = testUtils.getTestUser();
        userRepository.save(otherUser);
        otherToken = jwt().jwt(builder -> builder.subject(otherUser.getEmail()));
    }

    @AfterEach
    public void clean() {
        userRepository.deleteById(testUser.getId());
        userRepository.deleteById(otherUser.getId());
    }

    @Test
    public void testRegister() throws Exception {
        testUtils.generateData();
        var userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setNickname(testUtils.getTestUser().getNickname());
        userRegistrationDTO.setEmail(testUtils.getTestUser().getEmail());
        userRegistrationDTO.setPassword(testUtils.getPasswordInput());

        var request = post("/api/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userRegistrationDTO));
        mockMvc.perform(request).andExpect(status().isCreated());

        var user = userRepository.findByEmail(userRegistrationDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        assertThat(user.getId()).isPositive();
        assertThat(user.getNickname()).isEqualTo(userRegistrationDTO.getNickname());
        assertThat(user.getEmail()).isEqualTo(userRegistrationDTO.getEmail());
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
        testUtils.generateData();
        var user = testUtils.getTestUser();
        return Stream.of(
                Arguments.of("!@#$%^&*()[]{}", user.getEmail(), user.getPassword()),
                Arguments.of("", user.getEmail(), user.getPassword()),
                Arguments.of(null, user.getEmail(), user.getPassword()),
                Arguments.of(user.getNickname(), "not_email", user.getPassword()),
                Arguments.of(user.getNickname(), "", user.getPassword()),
                Arguments.of(user.getNickname(), null, user.getPassword()),
                Arguments.of(user.getNickname(), user.getEmail(), "qwerty"),
                Arguments.of(user.getNickname(), user.getEmail(), ""),
                Arguments.of(user.getNickname(), user.getEmail(), null)
        );
    }

    @Test
    public void testShowProfile() throws Exception {
        var request = get("/api/profile");
        mockMvc.perform(request).andExpect(status().isUnauthorized());
        mockMvc.perform(request.with(token)).andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(testUser.getNickname()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.role").value(testUser.getRole().name()))
                .andExpect(jsonPath("$.password").doesNotExist());
        mockMvc.perform(request.with(otherToken)).andExpect(jsonPath("$.email").value(otherUser.getEmail()));
    }

    @Test
    public void testUpdateProfile() throws Exception {
        testUtils.generateData();
        var userUpdateDto = new UserUpdateDTO();
        userUpdateDto.setNickname(JsonNullable.of(testUtils.getTestUser().getNickname()));
        userUpdateDto.setEmail(JsonNullable.of(testUtils.getTestUser().getEmail()));
        userUpdateDto.setPassword(JsonNullable.of(testUtils.getPasswordInput()));

        var request = put("/api/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userUpdateDto));
        mockMvc.perform(request).andExpect(status().isUnauthorized());
        mockMvc.perform(request.with(token)).andExpect(status().isOk());

        var updatedUser = userRepository.findById(testUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        assertThat(updatedUser.getNickname()).isEqualTo(userUpdateDto.getNickname().get());
        assertThat(updatedUser.getEmail()).isEqualTo(userUpdateDto.getEmail().get());
        assertThat(updatedUser.getPassword()).isNotEmpty().isNotEqualTo(testUser.getPassword())
                .isNotEqualTo(userUpdateDto.getPassword());
        assertThat(updatedUser.getCreatedAt()).isCloseTo(testUser.getCreatedAt(), within(1, ChronoUnit.MILLIS));
        assertThat(updatedUser.getUpdatedAt()).isNotEqualTo(testUser.getUpdatedAt());
    }

    @Test
    public void testPartialUpdateUser() throws Exception {
        testUtils.generateData();
        var userUpdateDto = new UserUpdateDTO();
        userUpdateDto.setNickname(JsonNullable.of(testUtils.getTestUser().getNickname()));

        var request = put("/api/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userUpdateDto));
        mockMvc.perform(request.with(token)).andExpect(status().isOk());

        var updatedUser = userRepository.findById(testUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        assertThat(updatedUser.getNickname()).isEqualTo(userUpdateDto.getNickname().get());
        assertThat(updatedUser.getEmail()).isEqualTo(testUser.getEmail());
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
        mockMvc.perform(request.with(token)).andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteUser() throws Exception {
        var id = testUser.getId();
        assertThat(userRepository.findById(id)).isPresent();
        var request = delete("/api/profile")
                .param("confirmation", "I want to delete my account permanently");
        mockMvc.perform(request).andExpect(status().isUnauthorized());
        mockMvc.perform(request.with(token)).andExpect(status().isNoContent());
        assertThat(userRepository.findById(id)).isEmpty();
    }
}
