package com.example.bid.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@Configuration
public class FeignOAuth2Config {

    private final OAuth2AuthorizedClientManager clientManager;

    public FeignOAuth2Config(OAuth2AuthorizedClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        return template -> {

            OAuth2AuthorizeRequest request =
                    OAuth2AuthorizeRequest
                            .withClientRegistrationId("keycloak")
                            .principal("bid-service")
                            .build();

            OAuth2AuthorizedClient client =
                    clientManager.authorize(request);

            if (client == null || client.getAccessToken() == null) {
                throw new IllegalStateException("Failed to get access token");
            }

            template.header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + client.getAccessToken().getTokenValue()
            );
        };
    }
}
