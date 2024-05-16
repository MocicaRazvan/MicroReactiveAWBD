package com.example.trainingservice.config;


import com.example.commonmodule.jackson.CustomObjectMapper;
import com.example.commonmodule.utils.EntitiesUtils;
import com.example.commonmodule.utils.PageableUtilsCustom;
import com.example.commonmodule.utils.RequestsUtils;
import com.example.commonmodule.utils.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeanConfig {


    @Bean
    public ObjectMapper customObjectMapper(final Jackson2ObjectMapperBuilder builder) {
        return new CustomObjectMapper(builder).customObjectMapper();
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
    @LoadBalanced
    public WebClient.Builder webClient() {
        return WebClient.builder();
    }

    @Bean
    public UserUtils userUtils(
            @Value("${user-service.url}") String userServiceUrl,
            CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry, RateLimiterRegistry rateLimiterRegistry
    ) {
        return new UserUtils(webClient(), userServiceUrl + "/users", "userService", circuitBreakerRegistry, retryRegistry, rateLimiterRegistry);
    }


}
