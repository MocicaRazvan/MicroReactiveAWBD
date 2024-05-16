package com.example.commonmodule.repositories;


import com.example.commonmodule.models.Approve;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@NoRepositoryBean
public interface ApprovedRepository<T extends Approve> extends TitleBodyRepository<T> {
    Flux<T> findAllByApproved(boolean approved, PageRequest request);

    Flux<T> findAllByTitleContainingIgnoreCaseAndApproved(String title, boolean approved, PageRequest pageRequest);

    Mono<Long> countAllByTitleContainingIgnoreCaseAndApproved(String title, boolean approved);

    Mono<Long> countByApproved(boolean approved);

    Mono<Long> countByUserId(Long userId);

    Mono<T> findByApprovedAndId(boolean approved, Long id);

    Mono<Boolean> existsByIdAndApprovedIsTrue(Long id);

    Flux<T> findAllByUserIdAndTitleContainingIgnoreCase(Long userId, String title, PageRequest pageRequest);

    Mono<Long> countAllByUserIdAndTitleContainingIgnoreCase(Long userId, String title);

    Flux<T> findAllByTitleContainingIgnoreCase(String title, PageRequest pageRequest);

    Mono<Long> countAllByTitleContainingIgnoreCase(String title);

}
