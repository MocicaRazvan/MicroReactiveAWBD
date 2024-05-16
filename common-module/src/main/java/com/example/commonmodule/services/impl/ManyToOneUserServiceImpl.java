package com.example.commonmodule.services.impl;


import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.dtos.generic.WithUser;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.dtos.response.ResponseWithUserDto;
import com.example.commonmodule.exceptions.action.PrivateRouteException;
import com.example.commonmodule.exceptions.notFound.NotFoundEntity;
import com.example.commonmodule.hateos.CustomEntityModel;
import com.example.commonmodule.mappers.DtoMapper;
import com.example.commonmodule.models.ManyToOneUser;
import com.example.commonmodule.repositories.ManyToOneUserRepository;
import com.example.commonmodule.services.ManyToOneUserService;
import com.example.commonmodule.utils.PageableUtilsCustom;
import com.example.commonmodule.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

@RequiredArgsConstructor
public abstract class ManyToOneUserServiceImpl<MODEL extends ManyToOneUser, BODY, RESPONSE extends WithUser,
        S extends ManyToOneUserRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>> implements ManyToOneUserService<MODEL, BODY, RESPONSE, S, M> {

    protected final S modelRepository;
    protected final M modelMapper;
    protected final PageableUtilsCustom pageableUtils;
    protected final UserUtils userUtils;
    //    protected final UserRepository userRepository;
//    protected final UserMapper userMapper;
    protected final String modelName;
    protected final List<String> allowedSortingFields;


    @Override
    public Mono<RESPONSE> deleteModel(Long id, String userId) {
        return userUtils.getUser("", userId)
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> privateRoute(true, authUser, model.getUserId())
                                .then(modelRepository.delete(model))
                                .then(Mono.fromCallable(() -> modelMapper.fromModelToResponse(model)))
                        )
                );
    }

    @Override
    public Mono<RESPONSE> getModelById(Long id, String userId) {
        return userUtils.getUser("", userId)
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> getResponseGuard(authUser, model, true))
                );
    }

    public Mono<RESPONSE> getResponseGuard(UserDto authUser, MODEL model, boolean guard) {
        return privateRoute(guard, authUser, model.getUserId())
                .thenReturn(modelMapper.fromModelToResponse(model));
    }

    @Override
    public Flux<PageableResponse<RESPONSE>> getAllModels(PageableBody pageableBody, String userId) {
        return pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
                .then(pageableUtils.createPageRequest(pageableBody))
                .flatMapMany(pr -> pageableUtils.createPageableResponse(
                                modelRepository.findAllBy(pr).map(modelMapper::fromModelToResponse),
                                modelRepository.count(),
                                pr
                        )

                );
    }


    @Override
    public Mono<RESPONSE> updateModel(Long id, BODY body, String userId) {
        return userUtils.getUser("", userId)
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> isNotAuthor(model, authUser)
                                .flatMap(isNotAuthor -> {
                                    if (isNotAuthor) {
                                        return Mono.error(new PrivateRouteException());
                                    } else {
                                        return modelMapper.updateModelFromBody(body, model)
                                                .flatMap(modelRepository::save)
                                                .map(modelMapper::fromModelToResponse);
                                    }
                                })
                        )
                );
    }

    @Override
    public Mono<ResponseWithUserDto<RESPONSE>> getModelByIdWithUser(Long id, String userId) {
        return userUtils.getUser("", userId)
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> getModelGuardWithUser(authUser, model, true)
                        )
                );

    }

    @Override
    public Flux<ResponseWithUserDto<RESPONSE>> getModelsWithUser(List<Long> ids, String userId) {
        return userUtils.getUser("", userId)
                .flatMapMany(authUser -> modelRepository.findAllById(ids)
                        .flatMap(model -> getModelGuardWithUser(authUser, model, false))
                );
    }


    public Mono<ResponseWithUserDto<RESPONSE>> getModelGuardWithUser(UserDto authUser, MODEL model, boolean guard) {
        return privateRoute(guard, authUser, model.getUserId())
                .then(userUtils.getUser("", model.getUserId().toString())
                        .map(user ->
                                ResponseWithUserDto.<RESPONSE>builder()
                                        .model(modelMapper.fromModelToResponse(model))
                                        .user(user)
                                        .build()
                        )

                );
    }


    public Mono<MODEL> getModel(Long id) {
        return modelRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundEntity(modelName, id)));
    }

    public Mono<Boolean> isNotAuthor(MODEL model, UserDto authUser) {
        return Mono.just(
                !model.getUserId().equals(authUser.getId())
        );
    }

    public Mono<Void> privateRoute(boolean guard, UserDto authUser, Long ownerId) {
        return userUtils.hasPermissionToModifyEntity(authUser, ownerId)
                .flatMap(perm -> {
                    if (guard && !perm) {
                        return Mono.error(new PrivateRouteException());
                    }
                    return Mono.empty();
                });
    }

    @Override
    public Flux<PageableResponse<RESPONSE>> getModelsByIdIn(List<Long> ids, PageableBody pageableBody) {
        return pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
                .then(pageableUtils.createPageRequest(pageableBody))
                .flatMapMany(pr -> pageableUtils.createPageableResponse(
                                modelRepository.findAllByIdIn(ids, pr).map(modelMapper::fromModelToResponse),
                                modelRepository.countAllByIdIn(ids),
                                pr
                        )

                );
    }

    @Override
    public Flux<RESPONSE> getModelsByIdIn(List<Long> ids) {
        return modelRepository.findAllByIdIn(ids).map(modelMapper::fromModelToResponse);
    }


}
