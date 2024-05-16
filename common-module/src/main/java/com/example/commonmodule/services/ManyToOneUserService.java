package com.example.commonmodule.services;


import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.generic.WithUser;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.dtos.response.ResponseWithUserDto;
import com.example.commonmodule.mappers.DtoMapper;
import com.example.commonmodule.models.ManyToOneUser;
import com.example.commonmodule.repositories.ManyToOneUserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ManyToOneUserService<MODEL extends ManyToOneUser, BODY, RESPONSE extends WithUser,
        S extends ManyToOneUserRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>> {
    Mono<RESPONSE> deleteModel(Long id, String userId);

    Mono<RESPONSE> getModelById(Long id, String userId);

    Flux<PageableResponse<RESPONSE>> getAllModels(PageableBody pageableBody, String userId);

    Mono<RESPONSE> updateModel(Long id, BODY body, String userId);

    Mono<ResponseWithUserDto<RESPONSE>> getModelByIdWithUser(Long id, String userId);

    Flux<ResponseWithUserDto<RESPONSE>> getModelsWithUser(List<Long> ids, String userId);

    Flux<PageableResponse<RESPONSE>> getModelsByIdIn(List<Long> ids, PageableBody pageableBody);

    Flux<RESPONSE> getModelsByIdIn(List<Long> ids);

}
