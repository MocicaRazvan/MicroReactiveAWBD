package com.example.commonmodule.clients;

import com.example.commonmodule.dtos.errors.BaseErrorResponse;
import com.example.commonmodule.dtos.errors.IdNameResponse;
import com.example.commonmodule.exceptions.action.IllegalActionException;
import com.example.commonmodule.exceptions.action.PrivateRouteException;
import com.example.commonmodule.exceptions.client.ThrowFallback;
import com.example.commonmodule.exceptions.common.ServiceCallFailedException;
import com.example.commonmodule.exceptions.notFound.NotFoundEntity;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class ClientBase {


    protected final String service;
    protected final WebClient.Builder webClientBuilder;
    protected final CircuitBreaker circuitBreaker;
    protected final Retry retry;
    protected final RateLimiter rateLimiter;

    public ClientBase(String service, WebClient.Builder webClientBuilder,
                      CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry, RateLimiterRegistry rateLimiterRegistry) {
        this.service = service;
        this.webClientBuilder = webClientBuilder;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(service);
        this.retry = retryRegistry.retry(service);
        this.rateLimiter = rateLimiterRegistry.rateLimiter(service);

    }

    protected Mono<? extends Throwable> handleClientException(ClientResponse response, String uri) {
        log.error("Status code: {}, uri: {}", response.statusCode(), uri);
        if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
            return response.bodyToMono(IdNameResponse.class)
                    .log()
                    .flatMap(idNameResponse -> Mono.error(new NotFoundEntity(idNameResponse.getName(), idNameResponse.getId())));
        } else if (response.statusCode().equals(HttpStatus.FORBIDDEN)) {
            return response.bodyToMono(BaseErrorResponse.class)
                    .flatMap(baseErrorResponse -> Mono.error(new PrivateRouteException()));
        } else if (response.statusCode().equals(HttpStatus.BAD_REQUEST)) {
            return response.bodyToMono(BaseErrorResponse.class)
                    .flatMap(baseErrorResponse -> Mono.error(new IllegalActionException(baseErrorResponse.getMessage())));
        } else if (response.statusCode().equals(HttpStatus.SERVICE_UNAVAILABLE) || response.statusCode().is5xxServerError()) {
            return Mono.error(new ThrowFallback());
        } else {
            return response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new ServiceCallFailedException(body, service, uri)));
        }


    }

    protected <T> Mono<T> handleWebRequestException(Throwable e) {
        log.error("Error: ", e);
        return Mono.error(new ThrowFallback());
    }


    public abstract WebClient getClient();
}
