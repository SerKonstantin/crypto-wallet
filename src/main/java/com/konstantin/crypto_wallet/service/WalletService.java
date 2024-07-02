package com.konstantin.crypto_wallet.service;

import com.konstantin.crypto_wallet.dto.wallet.WalletCreateDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletImportDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletUpdateDTO;
import com.konstantin.crypto_wallet.exception.ResourceAlreadyExistsException;
import com.konstantin.crypto_wallet.exception.ResourceNotFoundException;
import com.konstantin.crypto_wallet.mapper.WalletMapper;
import com.konstantin.crypto_wallet.model.User;
import com.konstantin.crypto_wallet.model.Wallet;
import com.konstantin.crypto_wallet.repository.UserRepository;
import com.konstantin.crypto_wallet.repository.WalletRepository;
import com.konstantin.crypto_wallet.util.SlugUtilsForWallet;
import com.konstantin.crypto_wallet.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

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
        var currentUser = userUtils.getCurrentUser();
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
        var currentUser = userUtils.getCurrentUser();
        return currentUser.getWallets()
                .stream()
                .map(walletMapper::map)
                .toList();
    }

    public WalletDTO getWalletBySlug(String slug) {
        var currentUser = userUtils.getCurrentUser();
        var wallet = getWalletByUserAndSlug(currentUser, slug);
        return walletMapper.map(wallet);
    }

    public WalletDTO updateWalletBySlug(String slug, WalletUpdateDTO walletUpdateDTO) {
        var currentUser = userUtils.getCurrentUser();
        var wallet = getWalletByUserAndSlug(currentUser, slug);

        if (currentUser.getWallets().stream().anyMatch(w -> w.getName().equals(walletUpdateDTO.getName()))) {
            throw new ResourceAlreadyExistsException("A wallet with this name already exists");
        }

        walletMapper.update(walletUpdateDTO, wallet);
        var updatedSlug = SlugUtilsForWallet.toUniqueSlug(wallet.getName(), currentUser.getWallets());
        wallet.setSlug(updatedSlug);
        walletRepository.save(wallet);
        return walletMapper.map(wallet);
    }

    public void deleteWallet(String slug) {
        var currentUser = userUtils.getCurrentUser();
        var wallet = getWalletByUserAndSlug(currentUser, slug);
        currentUser.getWallets().remove(wallet);
        walletRepository.delete(wallet);
        userRepository.save(currentUser);
    }

    public WalletDTO importWallet(WalletImportDTO walletImportDTO) {
        var currentUser = userUtils.getCurrentUser();
        var wallet = walletMapper.map(walletImportDTO);
        if (currentUser.getWallets().stream().anyMatch(w -> w.getName().equals(wallet.getName()))) {
            throw new ResourceAlreadyExistsException("A wallet with this name already exists");
        }

        Credentials credentials;
        try {
            credentials = Credentials.create(walletImportDTO.getPrivateKey());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid private key");
        }

        String address = credentials.getAddress();
        if (walletRepository.findByAddress(address).isPresent()) {
            throw new ResourceAlreadyExistsException("A wallet with this address already exists");
        }

        wallet.setAddress(address);
        String slug = SlugUtilsForWallet.toUniqueSlug(wallet.getName(), currentUser.getWallets());
        wallet.setSlug(slug);
        wallet.setUser(currentUser);

        currentUser.getWallets().add(wallet);

        walletRepository.save(wallet);
        userRepository.save(currentUser);

        return walletMapper.map(wallet);
    }

    private Wallet getWalletByUserAndSlug(User user, String slug) {
        return user.getWallets()
                .stream()
                .filter(w -> w.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }
}
