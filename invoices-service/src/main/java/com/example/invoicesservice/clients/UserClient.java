package com.example.invoicesservice.clients;


import com.example.invoicesservice.dtos.CustomEntityModel;
import com.example.invoicesservice.dtos.UserDto;
import com.example.invoicesservice.exceptions.ServiceUnreachable;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "user-service", path = "/users")
public interface UserClient {
    @GetMapping(value = "/{id}", headers = {"Accept=application/json"})
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    @Retry(name = "userService", fallbackMethod = "getUserFallback")
    @RateLimiter(name = "userService", fallbackMethod = "getUserFallback")
    CustomEntityModel<UserDto> getUser(@PathVariable Long id);

    default CustomEntityModel<UserDto> getUserFallback(Long id, Exception e) {
        throw new ServiceUnreachable("User not found with id: " + id + " due to user service issues", e);
    }
}
