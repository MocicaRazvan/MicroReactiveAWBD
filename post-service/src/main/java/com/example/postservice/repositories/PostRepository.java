package com.example.postservice.repositories;


import com.example.commonmodule.repositories.ApprovedRepository;
import com.example.postservice.models.Post;
import reactor.core.publisher.Mono;

public interface PostRepository extends ApprovedRepository<Post> {
    Mono<Boolean> existsByIdAndApprovedIsTrue(Long id);
}
