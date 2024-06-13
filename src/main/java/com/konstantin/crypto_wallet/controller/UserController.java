package com.konstantin.crypto_wallet.controller;

import com.konstantin.crypto_wallet.dto.user.UserDTO;
import com.konstantin.crypto_wallet.dto.user.UserRegistrationDTO;
import com.konstantin.crypto_wallet.dto.user.UserUpdateDTO;
import com.konstantin.crypto_wallet.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRegistrationDTO data) {
        var userDTO = userService.register(data);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityUtils.hasUserId(#id)")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        var userDTO = userService.getById(id);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@securityUtils.hasUserId(#id)")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDTO userDTO = userService.update(userUpdateDTO, id);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityUtils.hasUserId(#id)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, @RequestParam String confirmation) {
        if (!confirmation.equals("I want to delete my account permanently")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
