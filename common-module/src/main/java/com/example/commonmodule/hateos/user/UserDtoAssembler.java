package com.example.commonmodule.hateos.user;


import com.example.commonmodule.controllers.UserController;
import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.UserBody;
import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.enums.Role;
import com.example.commonmodule.hateos.CustomEntityModel;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;


@RequiredArgsConstructor
public class UserDtoAssembler implements ReactiveRepresentationModelAssembler<UserDto> {


    private final Class<? extends UserController> userController;

    @Override
    public Mono<CustomEntityModel<UserDto>> toModel(UserDto entity) {
        return Mono.just(CustomEntityModel.<UserDto>builder()
                        .content(entity)
                        .build())
                .flatMap(model ->
                        WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(userController).getUser(entity.getId()))
                                .withSelfRel()
                                .toMono()
                                .doOnNext(model::add).then(Mono.just(model)))
                .flatMap(model ->
                        WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(userController).updateUser(entity.getId(), UserBody.builder()
                                        .firstName("New first name").lastName("New last name")
                                        .build(), null))
                                .withRel("updateUser")
                                .toMono()
                                .doOnNext(model::add).then(Mono.just(model)))
                .flatMap(model ->
                        WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(userController).getAllUsers(
                                        PageableBody.builder()
                                                .page(0)
                                                .size(10)
                                                .sortingCriteria(Map.of("email", "asc"))
                                                .build(), "raz", Set.of(Role.ROLE_USER, Role.ROLE_TRAINER)))
                                .withRel(IanaLinkRelations.COLLECTION)
                                .toMono()
                                .doOnNext(model::add).then(Mono.just(model)))
                .flatMap(model ->
                        WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(userController).makeTrainer(entity.getId()))
                                .withRel("makeTrainer")
                                .toMono()
                                .doOnNext(model::add).then(Mono.just(model)))
                .flatMap(model ->
                        WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(userController).existsUserByIdAndRoleIn(entity.getId(), Set.of(Role.ROLE_USER, Role.ROLE_TRAINER)))
                                .withRel("existsUserByIdAndRoleIn")
                                .toMono()
                                .doOnNext(model::add).then(Mono.just(model)))
                ;
    }


}
