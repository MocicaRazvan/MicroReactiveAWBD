package com.example.invoicesservice.clients;


import com.example.invoicesservice.dtos.CustomEntityModel;
import com.example.invoicesservice.dtos.TrainingResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "training-service", path = "/trainings")
public interface TrainingClient {

    @GetMapping(value = "/internal/byIds", headers = {"Accept=application/json"})
    @CircuitBreaker(name = "trainingService", fallbackMethod = "getTrainingsByIdsFallback")
    @Retry(name = "trainingService", fallbackMethod = "getTrainingsByIdsFallback")
    @RateLimiter(name = "trainingService", fallbackMethod = "getTrainingsByIdsFallback")
    List<CustomEntityModel<TrainingResponse>> getTrainingsByIds(@RequestParam List<Long> ids);

    default List<CustomEntityModel<TrainingResponse>> getTrainingsByIdsFallback(List<Long> ids, Exception e) {
        return List.of();
    }
}
