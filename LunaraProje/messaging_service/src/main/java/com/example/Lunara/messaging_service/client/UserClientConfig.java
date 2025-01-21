package com.example.Lunara.messaging_service.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserClientConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public UserClientConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                String token = jwtTokenProvider.getToken(); // Tokeni alın
                requestTemplate.header("Authorization", "Bearer " + token); // Header ekle
            }
        };
    }
}

