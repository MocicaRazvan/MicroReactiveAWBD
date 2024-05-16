package com.example.trainingservice.clients;
///internal/validIds

import com.example.commonmodule.clients.ClientBase;
import com.example.commonmodule.dtos.response.EntityCount;
import com.example.commonmodule.dtos.response.ResponseWithUserDtoEntity;
import com.example.commonmodule.exceptions.action.IllegalActionException;
import com.example.commonmodule.exceptions.client.ThrowFallback;
import com.example.trainingservice.dto.exercises.ExerciseResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class ExerciseClient extends ClientBase {

    private final static String CLIENT_NAME = "exerciseService";

    @Value("${exercise-service.url}")
    private String exerciseServiceUrl;

    public ExerciseClient(WebClient.Builder webClientBuilder, CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry, RateLimiterRegistry rateLimiterRegistry) {
        super(CLIENT_NAME, webClientBuilder, circuitBreakerRegistry, retryRegistry, rateLimiterRegistry);
    }


    public WebClient getClient() {
        return webClientBuilder.baseUrl(exerciseServiceUrl + "/exercises").build();
    }

    public Mono<Void> verifyMappingExercises(List<Long> exercisesIds) {
        return getClient()
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path("/internal/validIds")
                                .queryParam("ids", exercisesIds)
                                .build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> handleClientException(response, exerciseServiceUrl))
                .bodyToMono(Void.class)
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .onErrorResume(WebClientRequestException.class, this::handleWebRequestException)
                .onErrorResume(ThrowFallback.class, e -> Mono.error(new IllegalActionException("Training cannot be created")));
    }

    public Flux<ResponseWithUserDtoEntity<ExerciseResponse>> getExercisesByIdsIn(List<Long> ids) {
        return getClient()
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path("/internal/byIds/withUser")
                                .queryParam("ids", ids)
                                .build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> handleClientException(response, exerciseServiceUrl))
                .bodyToFlux(new ParameterizedTypeReference<ResponseWithUserDtoEntity<ExerciseResponse>>() {
                })
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .onErrorResume(WebClientRequestException.class, this::handleWebRequestException)
                .onErrorResume(ThrowFallback.class, e -> Flux.empty());

    }
}
