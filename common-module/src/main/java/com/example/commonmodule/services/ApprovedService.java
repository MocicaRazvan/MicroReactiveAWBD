package com.example.commonmodule.services;


import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.dtos.generic.TitleBody;
import com.example.commonmodule.dtos.generic.WithUser;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.mappers.DtoMapper;
import com.example.commonmodule.models.Approve;
import com.example.commonmodule.repositories.ApprovedRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public interface ApprovedService<MODEL extends Approve, BODY extends TitleBody, RESPONSE extends WithUser,
        S extends ApprovedRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>>
        extends TitleBodyService<MODEL, BODY, RESPONSE, S, M> {
    Mono<RESPONSE> approveModel(Long id, String userId);

    Flux<PageableResponse<RESPONSE>> getModelsApproved(PageableBody pageableBody, String userId);

    Flux<PageableResponse<RESPONSE>> getModelsApproved(String title, PageableBody pageableBody, String userId);

    Mono<RESPONSE> createModel(BODY body, String userId);

    Flux<PageableResponse<RESPONSE>> getModelsTrainer(String title, Long trainerId, PageableBody pageableBody, String userId);

    Flux<PageableResponse<RESPONSE>> getAllModels(String title, PageableBody pageableBody, String userId);


//    Flux<PageableResponse<RESPONSE>> getModelsTitle(String title, boolean approved, PageableBody pageableBody);
}
