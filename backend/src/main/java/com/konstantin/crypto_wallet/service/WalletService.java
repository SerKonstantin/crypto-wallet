package com.konstantin.crypto_wallet.service;

import com.konstantin.crypto_wallet.dto.wallet.WalletCreateDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletImportDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletUpdateDTO;
import com.konstantin.crypto_wallet.exception.ResourceAlreadyExistsException;
import com.konstantin.crypto_wallet.mapper.WalletMapper;
import com.konstantin.crypto_wallet.repository.UserRepository;
import com.konstantin.crypto_wallet.repository.WalletRepository;
import com.konstantin.crypto_wallet.util.SlugUtilsForWallet;
import com.konstantin.crypto_wallet.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private SlugUtilsForWallet slugUtils;

    @Transactional
    public WalletDTO createWallet(WalletCreateDTO walletCreateDTO) {
        var currentUser = userUtils.getCurrentUser();
        var wallet = walletMapper.map(walletCreateDTO);
        if (currentUser.getWallets().stream().anyMatch(w -> w.getName().equals(wallet.getName()))) {
            throw new ResourceAlreadyExistsException("wallet name");
        }

        var slug = slugUtils.toUniqueSlug(wallet.getName(), currentUser.getWallets());
        wallet.setSlug(slug);
        wallet.setUser(currentUser);
        currentUser.getWallets().add(wallet);

        walletRepository.save(wallet);
        userRepository.save(currentUser);

        return walletMapper.map(wallet);
    }

    @Transactional(readOnly = true)
    public List<WalletDTO> getWallets() {
        var currentUser = userUtils.getCurrentUser();
        return currentUser.getWallets()
                .stream()
                .map(walletMapper::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public WalletDTO getWalletBySlug(String slug) {
        var currentUser = userUtils.getCurrentUser();
        var wallet = slugUtils.getWalletByUserAndSlug(currentUser, slug);
        return walletMapper.map(wallet);
    }

    @Transactional
    public WalletDTO updateWalletBySlug(String slug, WalletUpdateDTO walletUpdateDTO) {
        var currentUser = userUtils.getCurrentUser();
        var wallet = slugUtils.getWalletByUserAndSlug(currentUser, slug);

        if (currentUser.getWallets().stream().anyMatch(w -> w.getName().equals(walletUpdateDTO.getName()))) {
            throw new ResourceAlreadyExistsException("wallet");
        }

        walletMapper.update(walletUpdateDTO, wallet);
        var updatedSlug = slugUtils.toUniqueSlug(wallet.getName(), currentUser.getWallets());
        wallet.setSlug(updatedSlug);
        walletRepository.save(wallet);
        return walletMapper.map(wallet);
    }

    @Transactional
    public void deleteWallet(String slug) {
        var currentUser = userUtils.getCurrentUser();
        var wallet = slugUtils.getWalletByUserAndSlug(currentUser, slug);
        currentUser.getWallets().remove(wallet);
        walletRepository.delete(wallet);
        userRepository.save(currentUser);
    }

    @Transactional
    public WalletDTO importWallet(WalletImportDTO walletImportDTO) {
        var currentUser = userUtils.getCurrentUser();
        var wallet = walletMapper.map(walletImportDTO);
        if (currentUser.getWallets().stream().anyMatch(w -> w.getName().equals(wallet.getName()))) {
            throw new ResourceAlreadyExistsException("wallet");
        }

        Credentials credentials;
        try {
            credentials = Credentials.create(walletImportDTO.getPrivateKey());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid private key");
        }

        String address = credentials.getAddress();
        if (walletRepository.findByAddress(address).isPresent()) {
            throw new ResourceAlreadyExistsException("wallet");
        }

        wallet.setAddress(address);
        String slug = slugUtils.toUniqueSlug(wallet.getName(), currentUser.getWallets());
        wallet.setSlug(slug);
        wallet.setUser(currentUser);

        currentUser.getWallets().add(wallet);

        walletRepository.save(wallet);
        userRepository.save(currentUser);

        return walletMapper.map(wallet);
    }
}
