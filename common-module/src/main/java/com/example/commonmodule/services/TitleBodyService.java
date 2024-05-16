package com.example.commonmodule.services;


import com.example.commonmodule.dtos.generic.WithUser;
import com.example.commonmodule.dtos.response.ResponseWithUserLikesAndDislikes;
import com.example.commonmodule.mappers.DtoMapper;
import com.example.commonmodule.models.TitleBody;
import com.example.commonmodule.repositories.TitleBodyRepository;
import reactor.core.publisher.Mono;

public interface TitleBodyService<MODEL extends TitleBody, BODY, RESPONSE extends WithUser,
        S extends TitleBodyRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>>
        extends ManyToOneUserService<MODEL, BODY, RESPONSE, S, M> {

    Mono<RESPONSE> reactToModel(Long id, String type, String userId);

    Mono<ResponseWithUserLikesAndDislikes<RESPONSE>> getModelByIdWithUserLikesAndDislikes(Long id, String userId);


}
