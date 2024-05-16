package com.example.postservice.clients;


import com.example.commonmodule.clients.ClientBase;
import com.example.commonmodule.dtos.response.EntityCount;
import com.example.commonmodule.dtos.response.ResponseWithUserDto;
import com.example.commonmodule.exceptions.action.IllegalActionException;
import com.example.commonmodule.exceptions.client.ThrowFallback;
import com.example.commonmodule.utils.RequestsUtils;
import com.example.postservice.dtos.comments.CommentResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CommentClient extends ClientBase {

    private final static String CLIENT_NAME = "commentService";

    @Value("${comment-service.url}")
    private String commentServiceUrl;

    public CommentClient(WebClient.Builder webClientBuilder, CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry, RateLimiterRegistry rateLimiterRegistry) {
        super(CLIENT_NAME, webClientBuilder, circuitBreakerRegistry, retryRegistry, rateLimiterRegistry);
    }


    public WebClient getClient() {
        return webClientBuilder.baseUrl(commentServiceUrl + "/comments").build();
    }


    public Mono<Void> deleteCommentsByPostId(String postId, String userId) {
        return getClient()
                .delete()
                .uri(uriBuilder ->
                        uriBuilder.path("/internal/post/{postId}")
                                .build(postId)

                )
                .header(RequestsUtils.AUTH_HEADER, userId)
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> handleClientException(response, commentServiceUrl))
                .bodyToMono(Void.class)
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .onErrorResume(WebClientRequestException.class, this::handleWebRequestException)
                .onErrorResume(ThrowFallback.class, e -> Mono.error(new IllegalActionException("Post cannot be deleted")));

    }

    public Flux<ResponseWithUserDto<CommentResponse>> getCommentsByPost(String postId) {
        return getClient()
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path("/internal/post/{postId}")
                                .build(postId)

                )
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> handleClientException(response, commentServiceUrl))
                .bodyToFlux(new ParameterizedTypeReference<ResponseWithUserDto<CommentResponse>>() {
                })
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .onErrorResume(WebClientRequestException.class, this::handleWebRequestException)
                .onErrorResume(ThrowFallback.class, e -> Flux.empty());

    }


}
