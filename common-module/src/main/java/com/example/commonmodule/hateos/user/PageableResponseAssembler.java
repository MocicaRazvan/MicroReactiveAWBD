package com.example.commonmodule.hateos.user;


import com.example.commonmodule.controllers.UserController;
import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.enums.AuthProvider;
import com.example.commonmodule.enums.Role;
import com.example.commonmodule.hateos.CustomEntityModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@Slf4j
public abstract class PageableResponseAssembler<T, D extends ReactiveRepresentationModelAssembler<T>> {

    private final D itemAssembler;

    private final Class<? extends UserController> userController;


    public Mono<PageableResponse<CustomEntityModel<T>>> toModel(PageableResponse<T> pageableResponse, List<WebFluxLinkBuilder.WebFluxLink> additionalLinks) {
        return itemAssembler.toModel(pageableResponse.getContent())
                .map(c -> PageableResponse.<CustomEntityModel<T>>builder()
                        .content(c)
                        .pageInfo(pageableResponse.getPageInfo())
                        .links(additionalLinks)
                        .build());
    }


    public Mono<PageableResponse<CustomEntityModel<T>>> toModel(PageableResponse<T> pageableResponse) {
        List<WebFluxLinkBuilder.WebFluxLink> links = new ArrayList<>();
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(userController).getAllUsers(
                PageableBody.builder()
                        .page(0)
                        .size(10)
                        .sortingCriteria(Map.of("email", "asc"))
                        .build(), "raz", Set.of(Role.ROLE_USER, Role.ROLE_TRAINER), Set.of(AuthProvider.GOOGLE))).withSelfRel());
        return toModel(pageableResponse, links);
    }


}
