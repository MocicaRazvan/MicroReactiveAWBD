package com.example.commentservice.clients;


import com.example.commentservice.dtos.post.PostResponse;
import com.example.commonmodule.clients.ClientBase;
import com.example.commonmodule.dtos.response.EntityCount;
import com.example.commonmodule.exceptions.client.ThrowFallback;
import com.example.commonmodule.exceptions.notFound.NotFoundEntity;
import com.example.commonmodule.utils.RequestsUtils;
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

@Component

public class PostClient extends ClientBase {

    private final static String CLIENT_NAME = "postService";

    @Value("${post-service.url}")
    private String postServiceUrl;

    public PostClient(WebClient.Builder webClientBuilder, CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry, RateLimiterRegistry rateLimiterRegistry) {
        super(CLIENT_NAME, webClientBuilder, circuitBreakerRegistry, retryRegistry, rateLimiterRegistry);
    }


    public WebClient getClient() {
        return webClientBuilder.baseUrl(postServiceUrl + "/posts").build();
    }

    public Mono<Void> existsApprovedPost(String postId) {
        return getClient()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/internal/existsApproved/{postId}").build(postId))
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> handleClientException(response, postServiceUrl))
                .bodyToMono(Void.class)
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .onErrorResume(WebClientRequestException.class, this::handleWebRequestException)
                .onErrorResume(ThrowFallback.class, e -> Mono.error(new NotFoundEntity("post", Long.valueOf(postId))));
    }

    public Mono<PostResponse> getPostById(String postId, String userId) {
        return getClient()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/{postId}").build(postId))
                .accept(MediaType.APPLICATION_NDJSON)
                .header(RequestsUtils.AUTH_HEADER, userId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> handleClientException(response, postServiceUrl))
                .bodyToMono(PostResponse.class)
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .onErrorResume(WebClientRequestException.class, this::handleWebRequestException)
                .onErrorResume(ThrowFallback.class, e -> Mono.error(new NotFoundEntity("post", Long.valueOf(postId))));
    }


}
