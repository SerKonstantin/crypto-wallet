package com.konstantin.crypto_wallet.controller;

import com.konstantin.crypto_wallet.dto.transaction.TransactionRequestDTO;
import com.konstantin.crypto_wallet.dto.transaction.TransactionResponseDTO;
import com.konstantin.crypto_wallet.exception.PendingTransactionException;
import com.konstantin.crypto_wallet.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/wallets/{slug}/transactions")
    public ResponseEntity<TransactionResponseDTO> sendTransaction(
            @PathVariable String slug,
            @Valid @RequestBody TransactionRequestDTO requestDTO) throws Exception {
        try {
            var response = transactionService.sendTransaction(slug, requestDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PendingTransactionException e) {
            var pendingTransactionId = transactionService.getPendingTransactionId(slug);
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .header(HttpHeaders.LOCATION, "/api/wallets/" + slug + "/transactions/" + pendingTransactionId)
                    .build();
        }
    }

    @GetMapping("/wallets/{slug}/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> showTransactionHistoryByWallet(@PathVariable String slug) {
        var response = transactionService.getTransactionsByWalletSlug(slug);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> showTransactionDetails(@PathVariable Long transactionId) {
        var response = transactionService.getTransaction(transactionId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO Show all transactions (move from WalletController)

}
