package com.konstantin.crypto_wallet.service;

import com.konstantin.crypto_wallet.dto.user.UserDTO;
import com.konstantin.crypto_wallet.dto.user.UserRegistrationDTO;
import com.konstantin.crypto_wallet.dto.user.UserUpdateDTO;
import com.konstantin.crypto_wallet.exception.ResourceAlreadyExistsException;
import com.konstantin.crypto_wallet.mapper.UserMapper;
import com.konstantin.crypto_wallet.repository.UserRepository;
import com.konstantin.crypto_wallet.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserUtils userUtils;

    @Transactional
    public UserDTO register(UserRegistrationDTO data) {
        var user = userMapper.map(data);
        userUtils.normalize(user);
        if (userRepository.findByNicknameIgnoreCase(user.getNickname()).isPresent()) {
            throw new ResourceAlreadyExistsException("User with nickname " + user.getNickname() + " already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("User with email " + user.getEmail() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        userRepository.save(user);
        return userMapper.map(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getProfile() {
        var user = userUtils.getCurrentUser();
        return userMapper.map(user);
    }

    @Transactional
    public UserDTO updateProfile(UserUpdateDTO data) {
        var user = userUtils.getCurrentUser();
        userMapper.update(data, user);
        userUtils.normalize(user);
        if (data.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(data.getPassword().get()));
        }

        userRepository.save(user);
        return userMapper.map(user);
    }

    @Transactional
    public void deleteProfile() {
        var user = userUtils.getCurrentUser();
        userRepository.delete(user);
    }
}
