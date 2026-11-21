package com.anhnhvcoder.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;
import vn.payos.core.ClientOptions;

@Configuration
public class PayOSConfig {

    @Value("${payos.clientId}")
    private String clientId;
    @Value("${payos.apiKey}")
    private String apiKey;
    @Value("${payos.checksumKey}")
    private String checksumKey;

    @Bean
    public PayOS payOs(){
        ClientOptions options = ClientOptions.builder()
                .clientId(clientId)
                .apiKey(apiKey)
                .checksumKey(checksumKey)
                .build();
        return new PayOS(options);
    }

}
