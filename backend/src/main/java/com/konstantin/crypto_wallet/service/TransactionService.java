// Service work with wei. Convertation between wei and eth is handled by client side.

package com.konstantin.crypto_wallet.service;

import com.konstantin.crypto_wallet.dto.transaction.TransactionRequestDTO;
import com.konstantin.crypto_wallet.dto.transaction.TransactionResponseDTO;
import com.konstantin.crypto_wallet.exception.PendingTransactionException;
import com.konstantin.crypto_wallet.exception.ResourceNotFoundException;
import com.konstantin.crypto_wallet.mapper.TransactionMapper;
import com.konstantin.crypto_wallet.model.Wallet;
import com.konstantin.crypto_wallet.model.transaction.Transaction;
import com.konstantin.crypto_wallet.model.transaction.TransactionStatus;
import com.konstantin.crypto_wallet.model.transaction.TransactionType;
import com.konstantin.crypto_wallet.repository.TransactionRepository;
import com.konstantin.crypto_wallet.repository.WalletRepository;
import com.konstantin.crypto_wallet.tracker.TransactionTracker;
import com.konstantin.crypto_wallet.util.SlugUtilsForWallet;
import com.konstantin.crypto_wallet.util.UserUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
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
    private TransactionTracker transactionTracker;

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
    public TransactionResponseDTO sendTransaction(String walletSlug, TransactionRequestDTO requestDTO)
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
        transactionTracker.addTransaction(transaction);

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

    @Transactional
    public void checkAndUpdatePendingTransactions() {
        var pendingTransactions = transactionTracker.getPendingTransactions();

        pendingTransactions.forEach(transaction -> {
            var receiptOpt = web3j.ethGetTransactionReceipt(transaction.getTransactionHash())
                    .sendAsync().join().getTransactionReceipt();

            receiptOpt.ifPresent(receipt -> {
                var newStatus = receipt.isStatusOK() ? TransactionStatus.COMPLETED : TransactionStatus.FAILED;
                transaction.setStatus(newStatus);
                transactionRepository.save(transaction);
                transactionTracker.removeTransaction(transaction);
            });
        });
    }

    // Fetch new transactions from mined blocks
    // Includes incoming transactions and transactions sent from other apps/devices
    @PostConstruct
    public void setupBlockListener() {
        var executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.submit(() -> {
            try {
                web3j.blockFlowable(true).subscribe(this::processBlock, throwable -> {
                    System.err.println("Error in block listener: " + throwable.getMessage());
                    throwable.printStackTrace();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Transactional
    public void processBlock(EthBlock ethBlock) {
        var transactions = ethBlock.getBlock().getTransactions();
        transactions.forEach(tx -> {
            EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
            processTransactionFromBlock(transaction);
        });
    }

    private void processTransactionFromBlock(org.web3j.protocol.core.methods.response.Transaction transaction) {
        // Check and process RECEIVE transaction
        var receiverWalletOpt = walletRepository.findByAddress(transaction.getTo());
        receiverWalletOpt.ifPresent(wallet -> {
            if (!transactionRepository.existsByTransactionHashAndType(transaction.getHash(), TransactionType.RECEIVE)) {
                processTransaction(transaction, wallet, TransactionType.RECEIVE);
            }
        });

        // Check and process SEND transaction
        var senderWalletOpt = walletRepository.findByAddress(transaction.getFrom());
        senderWalletOpt.ifPresent(wallet -> {
            if (!transactionRepository.existsByTransactionHashAndType(transaction.getHash(), TransactionType.SEND)) {
                processTransaction(transaction, wallet, TransactionType.SEND);
            }
        });
    }

    private void processTransaction(
            org.web3j.protocol.core.methods.response.Transaction transaction,
            Wallet wallet,
            TransactionType type) {
        var newTransaction = createTransactionFromBlock(transaction, wallet, type);
        transactionRepository.save(newTransaction);
        transactionTracker.addTransaction(newTransaction);
    }

    private Transaction createTransactionFromBlock(
            org.web3j.protocol.core.methods.response.Transaction transaction,
            Wallet wallet,
            TransactionType type) {
        var newTransaction = new Transaction();
        newTransaction.setWallet(wallet);
        newTransaction.setFromAddress(transaction.getFrom());
        newTransaction.setToAddress(transaction.getTo());
        newTransaction.setTransactionHash(transaction.getHash());
        newTransaction.setStatus(TransactionStatus.PENDING);
        newTransaction.setType(type);

        if (type == TransactionType.RECEIVE) {
            newTransaction.setFee(BigInteger.ZERO);
            newTransaction.setTotal(transaction.getValue());
        } else {
            calculateSendTransactionFeeAndTotal(transaction, newTransaction);
        }

        return newTransaction;
    }

    private void calculateSendTransactionFeeAndTotal(
            org.web3j.protocol.core.methods.response.Transaction transaction,
            Transaction newTransaction) {
        try {
            var receipt = web3j.ethGetTransactionReceipt(transaction.getHash()).send().getTransactionReceipt()
                    .orElseThrow(() -> new RuntimeException("Transaction receipt not found"));
            var gasUsed = receipt.getGasUsed();
            var gasPrice = transaction.getGasPrice();
            var fee = gasPrice.multiply(gasUsed);
            newTransaction.setFee(fee);
            newTransaction.setTotal(transaction.getValue().add(fee));
        } catch (Exception e) {
            e.printStackTrace();
            newTransaction.setFee(BigInteger.ZERO);
            newTransaction.setTotal(transaction.getValue());
        }
    }
}
