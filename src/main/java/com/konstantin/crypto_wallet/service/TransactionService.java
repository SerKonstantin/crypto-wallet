// Service work with wei. Convertation between wei and eth is handled by client side.

package com.konstantin.crypto_wallet.service;

import com.konstantin.crypto_wallet.dto.transaction.TransactionRequestDTO;
import com.konstantin.crypto_wallet.dto.transaction.TransactionResponseDTO;
import com.konstantin.crypto_wallet.exception.PendingTransactionException;
import com.konstantin.crypto_wallet.exception.ResourceNotFoundException;
import com.konstantin.crypto_wallet.mapper.TransactionMapper;
import com.konstantin.crypto_wallet.model.transaction.Transaction;
import com.konstantin.crypto_wallet.model.transaction.TransactionStatus;
import com.konstantin.crypto_wallet.model.transaction.TransactionType;
import com.konstantin.crypto_wallet.repository.TransactionRepository;
import com.konstantin.crypto_wallet.repository.WalletRepository;
import com.konstantin.crypto_wallet.tracker.PendingTransactionTracker;
import com.konstantin.crypto_wallet.util.SlugUtilsForWallet;
import com.konstantin.crypto_wallet.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Numeric;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private Web3j web3j;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private SlugUtilsForWallet slugUtils;

    @Autowired
    private PendingTransactionTracker pendingTransactionTracker;

    @Transactional(readOnly = true)
    public boolean hasPendingTransaction(Long walletId) {
        return transactionRepository.existsByWalletIdAndStatus(walletId, TransactionStatus.PENDING);
    }


    @Transactional(readOnly = true)
    public Long getPendingTransactionId(String walletSlug) {
        var user = userUtils.getCurrentUser();
        var wallet = slugUtils.getWalletByUserAndSlug(user, walletSlug);
        var pendingTransaction = transactionRepository.findFirstByWalletIdAndStatusOrderByCreatedAtDesc(
                wallet.getId(),
                TransactionStatus.PENDING
        );
        return pendingTransaction.map(Transaction::getId).orElseThrow(
                () -> new ResourceNotFoundException("No pending transaction found")
        );
    }

    @Transactional
    public TransactionResponseDTO processTransaction(String walletSlug, TransactionRequestDTO requestDTO)
            throws Exception {
        var user = userUtils.getCurrentUser();
        var wallet = slugUtils.getWalletByUserAndSlug(user, walletSlug);

        if (hasPendingTransaction(wallet.getId())) {
            throw new PendingTransactionException("Transaction is in progress, please wait.");
        }

        var credentials = Credentials.create(requestDTO.getPrivateKey());
        if (!credentials.getAddress().equals(wallet.getAddress())) {
            throw new IllegalArgumentException("Invalid private key for the specified wallet address");
        }

        var balance = web3j.ethGetBalance(wallet.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance();
        var fee = requestDTO.getGasPrice().multiply(requestDTO.getGasLimit());
        var total = requestDTO.getAmount().add(fee);

        if (!fee.equals(requestDTO.getFee()) || !total.equals(requestDTO.getTotal())) {
            throw new IllegalArgumentException("Transaction data has been compromised");
        }

        if (balance.compareTo(total) < 0) {
            throw new IllegalArgumentException("Insufficient funds on wallet " + wallet.getName());
        }

        var nonce = web3j.ethGetTransactionCount(wallet.getAddress(), DefaultBlockParameterName.LATEST)
                .send()
                .getTransactionCount();
        var rawTransaction = RawTransaction.createEtherTransaction(
                nonce,
                requestDTO.getGasPrice(),
                requestDTO.getGasLimit(),
                requestDTO.getToAddress(),
                requestDTO.getAmount());
        var signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        var hexValue = Numeric.toHexString(signedMessage);

        var ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        var transactionHash = ethSendTransaction.getTransactionHash();

        if (transactionHash == null) {
            throw new RuntimeException("Transaction failed: " + ethSendTransaction.getError().getMessage());
        }

        var transaction = transactionMapper.map(requestDTO);
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.SEND);
        transaction.setTransactionHash(transactionHash);

        transactionRepository.save(transaction);
        pendingTransactionTracker.addTransaction(transaction);

        return transactionMapper.map(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactions(String walletSlug) {
        var user = userUtils.getCurrentUser();
        var wallet = slugUtils.getWalletByUserAndSlug(user, walletSlug);

        var transactions = transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());

        if (transactions == null || transactions.isEmpty()) {
            return new ArrayList<>();
        }

        return transactions.stream()
                .map(transactionMapper::map)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransactionResponseDTO getTransaction(String walletSlug, Long transactionId) {
        var user = userUtils.getCurrentUser();
        var wallet = slugUtils.getWalletByUserAndSlug(user, walletSlug);

        var transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        if (!wallet.getTransactions().contains(transaction)) {
            throw new ResourceNotFoundException("Transaction not found for wallet " + wallet.getName());
        }

        return transactionMapper.map(transaction);
    }
}
