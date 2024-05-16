package com.example.commonmodule.mappers;


import com.example.commonmodule.dtos.generic.WithUser;
import com.example.commonmodule.models.ManyToOneUser;
import reactor.core.publisher.Mono;

public abstract class DtoMapper<MODEL extends ManyToOneUser, BODY, RESPONSE extends WithUser> {

    public abstract RESPONSE fromModelToResponse(MODEL model);

    public abstract MODEL fromBodyToModel(BODY body);

    public abstract Mono<MODEL> updateModelFromBody(BODY body, MODEL model);

}
