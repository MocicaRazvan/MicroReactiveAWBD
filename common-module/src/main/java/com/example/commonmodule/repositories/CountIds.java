package com.example.commonmodule.repositories;

import reactor.core.publisher.Mono;

import java.util.List;

public interface CountIds {
    Mono<Long> countByIds(List<Long> ids);
}
