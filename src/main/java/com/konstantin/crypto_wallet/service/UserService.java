package com.konstantin.crypto_wallet.service;

import com.konstantin.crypto_wallet.dto.user.UserDTO;
import com.konstantin.crypto_wallet.dto.user.UserRegistrationDTO;
import com.konstantin.crypto_wallet.dto.user.UserUpdateDTO;
import com.konstantin.crypto_wallet.exception.ResourceNotFoundException;
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

    @Transactional
    public UserDTO register(UserRegistrationDTO data) {
        var user = userMapper.map(data);
        UserUtils.normalize(user);
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
    public UserDTO getById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.map(user);
    }

    @Transactional
    public UserDTO update(UserUpdateDTO data, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userMapper.update(data, user);
        UserUtils.normalize(user);
        if (data.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(data.getPassword().get()));
        }

        userRepository.save(user);
        return userMapper.map(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
