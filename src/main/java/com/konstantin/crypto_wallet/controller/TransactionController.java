package com.konstantin.crypto_wallet.controller;

import com.konstantin.crypto_wallet.dto.transaction.TransactionRequestDTO;
import com.konstantin.crypto_wallet.dto.transaction.TransactionResponseDTO;
import com.konstantin.crypto_wallet.dto.transaction.TransactionVerificationRequestDTO;
import com.konstantin.crypto_wallet.dto.transaction.TransactionVerificationResponseDTO;
import com.konstantin.crypto_wallet.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/precheck")
    public ResponseEntity<TransactionVerificationResponseDTO> verifyTransaction(
            @PathVariable String slug,
            @Valid @RequestBody TransactionVerificationRequestDTO requestDTO) throws Exception {
        var responseDTO = transactionService.verifyTransaction(slug, requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<TransactionResponseDTO> processTransaction(
            @PathVariable String slug,
            @Valid @RequestBody TransactionRequestDTO requestDTO) throws Exception {
        var responseDTO = transactionService.processTransaction(slug, requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionHistory(@PathVariable String slug) {
        // TODO: Insert method to get transaction history
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransactionDetails(
            @PathVariable String slug,
            @PathVariable Long transactionId) {
        // TODO: Insert method to view specific transaction details
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
