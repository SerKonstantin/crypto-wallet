package com.konstantin.crypto_wallet.config;

import com.konstantin.crypto_wallet.util.EnvironmentUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
@ConfigurationProperties(prefix = "infura")
@Getter
@Setter
public class Web3jConfig {

    // Url is received from properties file
    private String url;

    @Bean
    public Web3j web3j() {
        var apiKey = EnvironmentUtils.getEnvVariable("INFURA_API_KEY");
        return Web3j.build(new HttpService(url + apiKey));
    }

}
