package com.example.commonmodule.controllers;


import com.example.commonmodule.dtos.generic.WithUser;
import com.example.commonmodule.dtos.response.ResponseWithUserLikesAndDislikesEntity;
import com.example.commonmodule.hateos.CustomEntityModel;
import com.example.commonmodule.mappers.DtoMapper;
import com.example.commonmodule.models.TitleBody;
import com.example.commonmodule.repositories.TitleBodyRepository;
import com.example.commonmodule.services.TitleBodyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface TitleBodyController<MODEL extends TitleBody, BODY, RESPONSE extends WithUser,
        S extends TitleBodyRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>,
        G extends TitleBodyService<MODEL, BODY, RESPONSE, S, M>>
        extends ManyToOneUserController<MODEL, BODY, RESPONSE, S, M, G> {

    @PatchMapping("/like/{id}")
    Mono<ResponseEntity<CustomEntityModel<RESPONSE>>> likeModel(@PathVariable Long id, ServerWebExchange exchange);

    @PatchMapping("/dislike/{id}")
    Mono<ResponseEntity<CustomEntityModel<RESPONSE>>> dislikeModel(@PathVariable Long id, ServerWebExchange exchange);

    @GetMapping("/withUser/withReactions/{id}")
    Mono<ResponseEntity<ResponseWithUserLikesAndDislikesEntity<RESPONSE>>> getModelsWithUserAndReaction(@PathVariable Long id, ServerWebExchange exchange);
}
