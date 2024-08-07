package com.konstantin.crypto_wallet.controller;

import com.konstantin.crypto_wallet.dto.user.UserDTO;
import com.konstantin.crypto_wallet.dto.user.UserRegistrationDTO;
import com.konstantin.crypto_wallet.dto.user.UserUpdateDTO;
import com.konstantin.crypto_wallet.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRegistrationDTO data) {
        var userDTO = userService.register(data);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<UserDTO> showProfile() {
        var userDTO = userService.getProfile();
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<UserDTO> updateProfile(@Valid @RequestBody UserUpdateDTO data) {
        var userDTO = userService.updateProfile(data);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteProfile(@RequestParam String confirmation) {
        if (!confirmation.equals("I want to delete my account permanently")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        userService.deleteProfile();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
