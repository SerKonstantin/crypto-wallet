package com.konstantin.crypto_wallet.service;

import com.konstantin.crypto_wallet.dto.wallet.WalletCreateDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletUpdateDTO;
import com.konstantin.crypto_wallet.exception.ResourceNotFoundException;
import com.konstantin.crypto_wallet.mapper.WalletMapper;
import com.konstantin.crypto_wallet.repository.UserRepository;
import com.konstantin.crypto_wallet.repository.WalletRepository;
import com.konstantin.crypto_wallet.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private UserUtils userUtils;

    public WalletDTO createWallet(WalletCreateDTO walletCreateDTO) {
        var userId = userUtils.getCurrentUser().getId();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var wallet = walletMapper.map(walletCreateDTO);
        wallet.setUser(user);
        user.getWallets().add(wallet);

        walletRepository.save(wallet);
        userRepository.save(user);

        return walletMapper.map(wallet);
    }

    public List<WalletDTO> getWallets() {
        var userId = userUtils.getCurrentUser().getId();
        var wallets = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallets not found"));

        return wallets.stream()
                .map(walletMapper::map)
                .toList();
    }

    public WalletDTO getWalletById(Long walletId) {
        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        return walletMapper.map(wallet);
    }

    public WalletDTO updateWallet(Long walletId, WalletUpdateDTO walletUpdateDTO) {
        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        wallet.setName(walletUpdateDTO.getName());
        walletRepository.save(wallet);
        return walletMapper.map(wallet);
    }

    public void deleteWallet(Long walletId) {
        walletRepository.deleteById(walletId);
    }
}
