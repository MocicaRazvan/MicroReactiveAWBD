package com.example.commonmodule.hateos.user;

import com.example.commonmodule.hateos.CustomEntityModel;
import reactor.core.publisher.Mono;

public interface ReactiveRepresentationModelAssembler<T> {
//    Mono<EntityModel<T>> toModel(T entity);

    Mono<CustomEntityModel<T>> toModel(T entity);
}
