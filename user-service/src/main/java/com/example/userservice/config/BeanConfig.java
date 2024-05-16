package com.example.userservice.config;


import com.example.commonmodule.hateos.user.PageableUserAssembler;
import com.example.commonmodule.hateos.user.UserDtoAssembler;
import com.example.commonmodule.jackson.CustomObjectMapper;
import com.example.commonmodule.utils.EntitiesUtils;
import com.example.commonmodule.utils.PageableUtilsCustom;
import com.example.commonmodule.utils.RequestsUtils;
import com.example.userservice.controllers.UserControllerImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeanConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(authorizeExchangeSpec ->
                        authorizeExchangeSpec
                                .anyExchange().permitAll()  // Allow all requests without authentication
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable);  // Disable CSRF if necessary

        return http.build();
    }

    @Bean
    public ObjectMapper customObjectMapper(final Jackson2ObjectMapperBuilder builder) {
        return new CustomObjectMapper(builder).customObjectMapper();
    }

    @Bean
    public WebClient.Builder webClient() {
        return WebClient.builder();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RequestsUtils requestsUtils() {
        return new RequestsUtils();
    }

    @Bean
    public EntitiesUtils entitiesUtils() {
        return new EntitiesUtils();
    }

    @Bean
    public PageableUtilsCustom pageableUtilsCustom() {
        return new PageableUtilsCustom();
    }

    @Bean
    public PageableUserAssembler pageableUserAssembler() {
        return new PageableUserAssembler(new UserDtoAssembler(UserControllerImpl.class), UserControllerImpl.class);
    }

}
