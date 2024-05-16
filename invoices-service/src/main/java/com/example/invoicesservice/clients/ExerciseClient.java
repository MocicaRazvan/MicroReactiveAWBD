package com.example.invoicesservice.clients;


import com.example.invoicesservice.dtos.CustomEntityModel;
import com.example.invoicesservice.dtos.ExerciseResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "exercise-service", path = "/exercises")
public interface ExerciseClient {

    @GetMapping(value = "/internal/byIds", headers = {"Accept=application/json"})
    @CircuitBreaker(name = "exerciseService", fallbackMethod = "getExercisesByIdsFallback")
    @Retry(name = "exerciseService", fallbackMethod = "getExercisesByIdsFallback")
    @RateLimiter(name = "exerciseService", fallbackMethod = "getExercisesByIdsFallback")
    List<CustomEntityModel<ExerciseResponse>> getExercisesByIds(@RequestParam List<Long> ids);

    default List<CustomEntityModel<ExerciseResponse>> getExercisesByIdsFallback(List<Long> ids, Exception e) {
        return List.of();
    }

}
