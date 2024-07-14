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
@RequestMapping("/api/wallets/{slug}/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("")
    public ResponseEntity<TransactionResponseDTO> processTransaction(
            @PathVariable String slug,
            @Valid @RequestBody TransactionRequestDTO requestDTO) throws Exception {
        try {
            var response = transactionService.processTransaction(slug, requestDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PendingTransactionException e) {
            var pendingTransactionId = transactionService.getPendingTransactionId(slug);
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .header(HttpHeaders.LOCATION, "/api/wallets/" + slug + "/transactions/" + pendingTransactionId)
                    .build();
        }
    }

    @GetMapping("")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionHistory(@PathVariable String slug) {
        var response = transactionService.getTransactions(slug);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransactionDetails(
            @PathVariable String slug,
            @PathVariable Long transactionId) {
        // TODO: Insert method to view specific transaction details
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
