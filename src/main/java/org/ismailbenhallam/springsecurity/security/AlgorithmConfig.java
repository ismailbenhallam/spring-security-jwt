package org.ismailbenhallam.springsecurity.security;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlgorithmConfig {

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Bean
    public Algorithm algorithm() {
        return Algorithm.HMAC512(secretKey.getBytes());
    }
}
