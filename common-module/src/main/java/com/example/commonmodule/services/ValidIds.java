package com.example.commonmodule.services;

import reactor.core.publisher.Mono;

import java.util.List;

public interface ValidIds {

    Mono<Void> validIds(List<Long> ids);
}
