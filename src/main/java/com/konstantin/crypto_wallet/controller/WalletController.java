package com.konstantin.crypto_wallet.controller;

import com.konstantin.crypto_wallet.dto.wallet.WalletCreateDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletImportDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletUpdateDTO;
import com.konstantin.crypto_wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("")
    public ResponseEntity<WalletDTO> createWallet(@Valid @RequestBody WalletCreateDTO data) {
        var walletDTO = walletService.createWallet(data);
        return new ResponseEntity<>(walletDTO, HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<List<WalletDTO>> showWallets() {
        var walletsDTO = walletService.getWallets();
        return new ResponseEntity<>(walletsDTO, HttpStatus.OK);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<WalletDTO> shotWallet(@PathVariable String slug) {
        var walletDTO = walletService.getWalletBySlug(slug);
        return new ResponseEntity<>(walletDTO, HttpStatus.OK);
    }

    @PutMapping("/{slug}")
    public ResponseEntity<WalletDTO> updateWallet(@PathVariable String slug, @Valid @RequestBody WalletUpdateDTO data) {
        var walletDTO = walletService.updateWalletBySlug(slug, data);
        return new ResponseEntity<>(walletDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deleteWallet(@PathVariable String slug, @RequestParam String confirmation) {
        if (!confirmation.equals("I want to delete this wallet permanently")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        walletService.deleteWallet(slug);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/import")
    public ResponseEntity<WalletDTO> importWallet(@Valid @RequestBody WalletImportDTO data) {
        var walletDTO = walletService.importWallet(data);
        return new ResponseEntity<>(walletDTO, HttpStatus.CREATED);
    }
}
