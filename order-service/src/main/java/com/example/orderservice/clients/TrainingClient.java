package com.example.orderservice.clients;

import com.example.commonmodule.clients.ClientBase;
import com.example.commonmodule.dtos.response.EntityCount;
import com.example.commonmodule.exceptions.action.IllegalActionException;
import com.example.commonmodule.exceptions.client.ThrowFallback;
import com.example.orderservice.dtos.trainings.TotalPrice;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class TrainingClient extends ClientBase {
    private final static String CLIENT_NAME = "trainingService";

    @Value("${training-service.url}")
    private String trainingServiceUrl;

    public TrainingClient(WebClient.Builder webClientBuilder, CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry, RateLimiterRegistry rateLimiterRegistry) {
        super(CLIENT_NAME, webClientBuilder, circuitBreakerRegistry, retryRegistry, rateLimiterRegistry);
    }


    public WebClient getClient() {
        return webClientBuilder.baseUrl(trainingServiceUrl + "/trainings").build();
    }

    public Mono<Void> verifyMappingTrainings(List<Long> trainingIds) {
        return getClient()
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path("/internal/validIds")
                                .queryParam("ids", trainingIds)
                                .build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> handleClientException(response, trainingServiceUrl))
                .bodyToMono(Void.class)
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .onErrorResume(WebClientRequestException.class, this::handleWebRequestException)
                .onErrorResume(ThrowFallback.class, e -> Mono.error(new IllegalActionException("Order cannot be created")));
    }

    public Mono<TotalPrice> getTotalPrice(List<Long> trainingIds) {
        return getClient()
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path("/internal/totalPrice")
                                .queryParam("ids", trainingIds)
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> handleClientException(response, trainingServiceUrl))
                .bodyToMono(TotalPrice.class)
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .onErrorResume(WebClientRequestException.class, this::handleWebRequestException)
                .onErrorResume(ThrowFallback.class, e -> Mono.error(new IllegalActionException("Order cannot be payed")));

    }

}
