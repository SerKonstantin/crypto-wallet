package com.konstantin.crypto_wallet.service;

import com.konstantin.crypto_wallet.dto.wallet.WalletCreateDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletUpdateDTO;
import com.konstantin.crypto_wallet.exception.ResourceAlreadyExistsException;
import com.konstantin.crypto_wallet.exception.ResourceNotFoundException;
import com.konstantin.crypto_wallet.mapper.WalletMapper;
import com.konstantin.crypto_wallet.model.Wallet;
import com.konstantin.crypto_wallet.repository.UserRepository;
import com.konstantin.crypto_wallet.repository.WalletRepository;
import com.konstantin.crypto_wallet.util.SlugUtilsForWallet;
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
        // We use userId to be sure that user from security context exists in database
        var userId = userUtils.getCurrentUser().getId();
        var currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var wallet = walletMapper.map(walletCreateDTO);
        if (currentUser.getWallets().stream().anyMatch(w -> w.getName().equals(wallet.getName()))) {
            throw new ResourceAlreadyExistsException("A wallet with this name already exists");
        }

        var slug = SlugUtilsForWallet.toUniqueSlug(wallet.getName(), currentUser.getWallets());
        wallet.setSlug(slug);
        wallet.setUser(currentUser);
        currentUser.getWallets().add(wallet);

        walletRepository.save(wallet);
        userRepository.save(currentUser);

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

    public WalletDTO getWalletBySlug(String slug) {
        var userId = userUtils.getCurrentUser().getId();
        var wallet = getWalletByUserIdAndSlug(userId, slug);
        return walletMapper.map(wallet);
    }

    public WalletDTO updateWalletBySlug(String slug, WalletUpdateDTO walletUpdateDTO) {
        var userId = userUtils.getCurrentUser().getId();
        var wallet = getWalletByUserIdAndSlug(userId, slug);
        var wallets = wallet.getUser().getWallets();

        if (wallets.stream().anyMatch(w -> w.getName().equals(walletUpdateDTO.getName()))) {
            throw new ResourceAlreadyExistsException("A wallet with this name already exists");
        }

        walletMapper.update(walletUpdateDTO, wallet);
        var updatedSlug = SlugUtilsForWallet.toUniqueSlug(wallet.getName(), wallets);
        wallet.setSlug(updatedSlug);
        walletRepository.save(wallet);
        return walletMapper.map(wallet);
    }

    public void deleteWallet(String slug) {
        var userId = userUtils.getCurrentUser().getId();
        var wallet = getWalletByUserIdAndSlug(userId, slug);
        wallet.getUser().getWallets().remove(wallet);
        walletRepository.delete(wallet);
    }

    private Wallet getWalletByUserIdAndSlug(Long userId, String slug) {
        var wallets = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallets not found"));
        return wallets.stream()
                .filter(w -> w.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }
}
