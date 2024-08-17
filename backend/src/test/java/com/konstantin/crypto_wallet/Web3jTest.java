package com.konstantin.crypto_wallet;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.protocol.Web3j;

@SpringBootTest
public class Web3jTest {

    @Autowired
    private Web3j web3j;

    @Test
    public void testWeb3jConnection() throws Exception {
        try {
            var clientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();
            assertThat(clientVersion).isNotNull();
            var blockNumber = web3j.ethBlockNumber().send();
            assertThat(blockNumber.getBlockNumber()).isNotNull();
        } catch (Exception e) {
            System.out.println("testWeb3jConnection fails with message: " + e.getMessage());
            assertThat(e).isNull();
        }
    }
}
