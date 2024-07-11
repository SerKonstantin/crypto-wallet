// Service work with wei. Convertation between wei and eth is handled by client side.

package com.konstantin.crypto_wallet.service;

import com.konstantin.crypto_wallet.dto.transaction.TransactionRequestDTO;
import com.konstantin.crypto_wallet.dto.transaction.TransactionResponseDTO;
import com.konstantin.crypto_wallet.dto.transaction.TransactionVerificationRequestDTO;
import com.konstantin.crypto_wallet.dto.transaction.TransactionVerificationResponseDTO;
import com.konstantin.crypto_wallet.mapper.TransactionMapper;
import com.konstantin.crypto_wallet.repository.TransactionRepository;
import com.konstantin.crypto_wallet.repository.WalletRepository;
import com.konstantin.crypto_wallet.util.JWTUtils;
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

import java.math.BigInteger;
import java.util.Map;

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
    private JWTUtils jwtUtils;

    @Transactional(readOnly = true)
    public TransactionVerificationResponseDTO verifyTransaction(String walletSlug, TransactionVerificationRequestDTO requestDTO) throws Exception {
        var user = userUtils.getCurrentUser();
        var wallet = slugUtils.getWalletByUserAndSlug(user, walletSlug);

        BigInteger balance = web3j.ethGetBalance(wallet.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance();
        var fee = requestDTO.getGasPrice().multiply(requestDTO.getGasLimit());
        var total = requestDTO.getAmount().add(fee);
        var sufficientFunds = balance.compareTo(total) >= 0;

        // Token to verify that input from client side won't change after checking if user have sufficient funds
        Map<String, Object> claimsMap = Map.of(
                "fromAddress", wallet.getAddress(),
                "toAddress", requestDTO.getToAddress(),
                "amount", requestDTO.getAmount(),
                "fee", fee,
                "total", total
        );
        var token = jwtUtils.generateTransactionVerificationToken(claimsMap);

        var responseDTO = new TransactionVerificationResponseDTO();
        responseDTO.setFromAddress(wallet.getAddress());
        responseDTO.setToAddress(requestDTO.getToAddress());
        responseDTO.setAmount(requestDTO.getAmount());
        responseDTO.setFee(fee);
        responseDTO.setTotal(total);
        responseDTO.setSufficientFunds(sufficientFunds);
        responseDTO.setVerificationToken(token);

        return responseDTO;
    }

    @Transactional
    public TransactionResponseDTO processTransaction(String walletSlug, TransactionRequestDTO requestDTO) throws Exception {
        var user = userUtils.getCurrentUser();
        var wallet = slugUtils.getWalletByUserAndSlug(user, walletSlug);

        var credentials = Credentials.create(requestDTO.getPrivateKey());
        if (!credentials.getAddress().equals(wallet.getAddress())) {
            throw new IllegalArgumentException("Invalid private key for the specified wallet address");
        }

        var fee = requestDTO.getGasPrice().multiply(requestDTO.getGasLimit());
        var total = requestDTO.getAmount().add(fee);

        Map<String, Object> claims = jwtUtils.validateTransactionVerificationToken(requestDTO.getVerificationToken());
        var verifiedToAddress = (String) claims.get("toAddress");
        var verifiedAmount = (BigInteger) claims.get("amount");
        var verifiedFee = (BigInteger) claims.get("fee");
        var verifiedTotal = (BigInteger) claims.get("total");

        if (!verifiedToAddress.equals(requestDTO.getToAddress()) || !verifiedAmount.equals(requestDTO.getAmount()) || !verifiedFee.equals(fee) || !verifiedTotal.equals(total)) {
            throw new IllegalArgumentException("Transaction data has been tampered with");
        }

        var nonce = web3j.ethGetTransactionCount(wallet.getAddress(), DefaultBlockParameterName.LATEST).send().getTransactionCount();

        var rawTransaction = RawTransaction.createEtherTransaction(nonce, requestDTO.getGasPrice(), requestDTO.getGasLimit(), requestDTO.getToAddress(), requestDTO.getAmount());
        var signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        var hexValue = Numeric.toHexString(signedMessage);

        var ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
        var transactionHash = ethSendTransaction.getTransactionHash();

        if (transactionHash == null) {
            throw new RuntimeException("Transaction failed: " + ethSendTransaction.getError().getMessage());
        }

        var transaction = transactionMapper.map(requestDTO);
        transaction.setWallet(wallet);
        transaction.setFromAddress(wallet.getAddress());
        transaction.setTransactionHash(transactionHash);

        transactionRepository.save(transaction);

        return transactionMapper.map(transaction);
    }
}
