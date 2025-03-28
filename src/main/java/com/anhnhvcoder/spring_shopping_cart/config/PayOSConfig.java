package com.anhnhvcoder.spring_shopping_cart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

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
        return new PayOS(clientId, apiKey, checksumKey);
    }

}
