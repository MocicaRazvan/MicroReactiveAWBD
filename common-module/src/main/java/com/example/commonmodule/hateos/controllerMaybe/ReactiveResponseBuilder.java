package com.example.commonmodule.hateos.controllerMaybe;


import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.dtos.response.*;
import com.example.commonmodule.hateos.CustomEntityModel;
import com.example.commonmodule.hateos.user.UserDtoAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.BiFunction;


@RequiredArgsConstructor
public class ReactiveResponseBuilder<RESPONSE, C> {

    protected final ReactiveLinkBuilder<RESPONSE, C> linkBuilder;

    public Mono<CustomEntityModel<RESPONSE>> toModel(RESPONSE response, Class<C> clazz) {
        CustomEntityModel<RESPONSE> model = CustomEntityModel.of(response);
        List<Mono<Link>> links = linkBuilder.createModelLinks(response, clazz)
                .stream().map(WebFluxLinkBuilder.WebFluxLink::toMono).toList();

        return Flux.merge(links)
                .collectList()
                .doOnNext(model::add)
                .thenReturn(model);

    }

    public Mono<ResponseWithUserDtoEntity<RESPONSE>> toModelWithUser(ResponseWithUserDto<RESPONSE> response, Class<C> clazz) {
        return toModel(response.getModel(), clazz)
                .map(model -> {
                            ResponseWithUserDtoEntity<RESPONSE> entity = new ResponseWithUserDtoEntity<>();
                            entity.setUser(response.getUser());
                            entity.setModel(model);
                            return entity;
                        }
                );
    }

    public Mono<ResponseWithUserLikesAndDislikesEntity<RESPONSE>> toModelWithUserLikesAndDislikes(ResponseWithUserLikesAndDislikes<RESPONSE> response, Class<C> clazz) {
        return toModelWithUser(response, clazz)
                .map(withUser -> {
                            ResponseWithUserLikesAndDislikesEntity<RESPONSE> entity = new ResponseWithUserLikesAndDislikesEntity<>();
                            entity.setUserLikes(response.getUserLikes());
                            entity.setUserDislikes(response.getUserDislikes());
                            entity.setUser(withUser.getUser());
                            entity.setModel(withUser.getModel());
                            return entity;
                        }
                );
    }

    public Mono<PageableResponse<CustomEntityModel<RESPONSE>>> toModelPageable(PageableResponse<RESPONSE> pageableResponse, Class<C> clazz) {
        return toModelGeneric(pageableResponse, clazz, this::toModel);
    }

    public Mono<PageableResponse<ResponseWithUserDtoEntity<RESPONSE>>> toModelWithUserPageable(PageableResponse<ResponseWithUserDto<RESPONSE>> response, Class<C> clazz) {
        return toModelGeneric(response, clazz, this::toModelWithUser);
    }

    public Mono<PageableResponse<ResponseWithUserLikesAndDislikesEntity<RESPONSE>>> toModelWithUserLikesAndDislikesPageable(PageableResponse<ResponseWithUserLikesAndDislikes<RESPONSE>> response, Class<C> clazz) {
        return toModelGeneric(response, clazz, this::toModelWithUserLikesAndDislikes);
    }


    private <T, R> Mono<PageableResponse<R>> toModelGeneric(
            PageableResponse<T> response,
            Class<C> clazz,
            BiFunction<T, Class<C>, Mono<R>> conversionFunction) {

        return conversionFunction.apply(response.getContent(), clazz)
                .map(c -> PageableResponse.<R>builder()
                        .content(c)
                        .pageInfo(response.getPageInfo())
                        .build());
    }


}
