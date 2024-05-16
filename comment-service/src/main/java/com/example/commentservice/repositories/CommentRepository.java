package com.example.commentservice.repositories;


import com.example.commentservice.models.Comment;
import com.example.commonmodule.repositories.TitleBodyRepository;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentRepository extends TitleBodyRepository<Comment> {

    Flux<Comment> findAllByPostId(Long postId, PageRequest pageRequest);

    Mono<Long> countAllByPostId(Long postId);

    Mono<Long> countAllByUserId(Long userId);

    Flux<Comment> findAllByPostId(Long postId);

    Mono<Void> deleteAllByPostIdEquals(Long postId);
}
