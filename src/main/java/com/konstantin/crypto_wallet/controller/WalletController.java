package com.konstantin.crypto_wallet.controller;

import com.konstantin.crypto_wallet.dto.wallet.WalletCreateDTO;
import com.konstantin.crypto_wallet.dto.wallet.WalletDTO;
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

    @GetMapping("/{id}")
    public ResponseEntity<WalletDTO> shotWalletById(@PathVariable Long id) {
        var walletDTO = walletService.getWalletById(id);
        return new ResponseEntity<>(walletDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WalletDTO> updateWallet(@PathVariable Long id, @Valid @RequestBody WalletUpdateDTO data) {
        var walletDTO = walletService.updateWallet(id, data);
        return new ResponseEntity<>(walletDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long id, @RequestParam String confirmation) {
        if (!confirmation.equals("I want to delete this wallet permanently")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        walletService.deleteWallet(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
