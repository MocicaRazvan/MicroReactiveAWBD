package com.example.commonmodule.utils;

import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.enums.Role;
import com.example.commonmodule.exceptions.action.IllegalActionException;
import com.example.commonmodule.exceptions.client.ThrowFallback;
import com.example.commonmodule.exceptions.common.ServiceCallFailedException;
import com.example.commonmodule.exceptions.notFound.NotFoundEntity;
import com.example.commonmodule.hateos.CustomEntityModel;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class UserUtils {

    private final WebClient.Builder userClient;
    private final String baseUrl;


    protected final String service;
    protected final CircuitBreaker circuitBreaker;
    protected final Retry retry;
    protected final RateLimiter rateLimiter;

    public UserUtils(WebClient.Builder userClient, String baseUrl, String service,
                     CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry, RateLimiterRegistry rateLimiterRegistry) {
        this.service = service;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(service);
        this.retry = retryRegistry.retry(service);
        this.rateLimiter = rateLimiterRegistry.rateLimiter(service);
        this.userClient = userClient;
        this.baseUrl = baseUrl;

    }


    public WebClient getUserClient() {
        return userClient.baseUrl(baseUrl).build();
    }


    public Mono<UserDto> getUser(String uri, String userId) {
        return getUser(uri + "/" + userId);
    }

    public Mono<UserDto> getUser(String uri) {
        return getUserClient()
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> handleNotFoundException(response, uri))
                .bodyToMono(new ParameterizedTypeReference<CustomEntityModel<UserDto>>() {
                })
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .onErrorResume(WebClientRequestException.class, this::handleWebRequestException)
                .onErrorResume(ThrowFallback.class, e -> Mono.error(new NotFoundEntity(
                        "user", Long.valueOf(uri.substring(uri.lastIndexOf("/") + 1))
                )))
                .map(CustomEntityModel::getContent)
                ;


    }

    private Mono<? extends Throwable> handleNotFoundException(ClientResponse response, String uri) {
        log.info(response.toString());
        if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
            return response.bodyToMono(NotFoundEntity.class)
                    .flatMap(Mono::error);
        } else {
            return response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new ServiceCallFailedException(body, "user-service", uri)));
        }
    }

    public Mono<Boolean> hasPermissionToModifyEntity(UserDto authUser, Long entityUserId) {
        return Mono.just(authUser.getRole() == Role.ROLE_ADMIN || Objects.equals(authUser.getId(), entityUserId));
    }

    public Mono<Void> existsUser(String uri, String trainerId, List<Role> roles) {
        return existsUser(uri + "/" + trainerId, roles);
    }

    // exists/{trainerId}
    public Mono<Void> existsTrainerOrAdmin(String uri, Long trainerId) {
        return existsUser(uri + "/" + trainerId, List.of(Role.ROLE_TRAINER, Role.ROLE_ADMIN));
    }

    public Mono<Void> existsUser(String uri, List<Role> roles) {
        List<Role> rolesList = roles == null ? new ArrayList<>() : roles;
        return getUserClient()
                .get()
                .uri(uriBuilder -> uriBuilder.path(uri).queryParam("roles", rolesList.stream()
                        .map(Role::toString).toList()
                ).build())
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> handleNotFoundException(response, uri))
                .bodyToMono(Void.class)
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .onErrorResume(WebClientRequestException.class, this::handleWebRequestException)
                .onErrorResume(ThrowFallback.class, e -> Mono.error(new NotFoundEntity(
                        "user", Long.valueOf(uri.substring(uri.lastIndexOf("/") + 1))
                )));
    }


    // byIds
    public Flux<UserDto> getUsersByIdIn(String uri, List<Long> ids) {
        return getUserClient()
                .get()
                .uri(uriBuilder -> {
                    URI builtUri = uriBuilder.path(uri).queryParam("ids", ids).build();
                    log.debug("Calling URI: {}", builtUri);
                    return builtUri;
                })
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> handleNotFoundException(response, uri))
                .bodyToFlux(new ParameterizedTypeReference<CustomEntityModel<UserDto>>() {
                })
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .onErrorResume(WebClientRequestException.class, this::handleWebRequestException)
                .onErrorResume(ThrowFallback.class, e -> Flux.empty())
                .map(CustomEntityModel::getContent);
    }


    private <T> Mono<T> handleWebRequestException(Throwable e) {
        log.error("Error: ", e);
        return Mono.error(new ThrowFallback());
    }
}
