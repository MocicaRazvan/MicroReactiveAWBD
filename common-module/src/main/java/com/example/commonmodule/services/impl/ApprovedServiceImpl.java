package com.example.commonmodule.services.impl;


import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.dtos.generic.TitleBody;
import com.example.commonmodule.dtos.generic.WithUser;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.dtos.response.ResponseWithUserDto;
import com.example.commonmodule.dtos.response.ResponseWithUserLikesAndDislikes;
import com.example.commonmodule.enums.Role;
import com.example.commonmodule.exceptions.action.IllegalActionException;
import com.example.commonmodule.exceptions.action.PrivateRouteException;
import com.example.commonmodule.mappers.DtoMapper;
import com.example.commonmodule.models.Approve;
import com.example.commonmodule.repositories.ApprovedRepository;
import com.example.commonmodule.services.ApprovedService;
import com.example.commonmodule.utils.EntitiesUtils;
import com.example.commonmodule.utils.PageableUtilsCustom;
import com.example.commonmodule.utils.UserUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.List;

public abstract class ApprovedServiceImpl<MODEL extends Approve, BODY extends TitleBody, RESPONSE extends WithUser,
        S extends ApprovedRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>>
        extends TitleBodyServiceImpl<MODEL, BODY, RESPONSE, S, M>
        implements ApprovedService<MODEL, BODY, RESPONSE, S, M> {


    public ApprovedServiceImpl(S modelRepository, M modelMapper, PageableUtilsCustom pageableUtils, UserUtils userUtils, String modelName, List<String> allowedSortingFields, EntitiesUtils entitiesUtils) {
        super(modelRepository, modelMapper, pageableUtils, userUtils, modelName, allowedSortingFields, entitiesUtils);
    }

    @Override
    public Mono<RESPONSE> approveModel(Long id, String userId) {
        return getModel(id)
                .flatMap(model -> {
                    if (model.isApproved()) {
                        return Mono.error(new IllegalActionException(modelName + " with id " + id + " is already approved!"));
                    }
                    model.setApproved(true);
                    return modelRepository.save(model);
                })
                .map(modelMapper::fromModelToResponse);

    }

    @Override
    public Flux<PageableResponse<RESPONSE>> getModelsApproved(PageableBody pageableBody, String userId) {
//        return pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
//                .then(pageableUtils.createPageRequest(pageableBody))
//                .flatMapMany(pr -> pageableUtils.createPageableResponse(
//                                modelRepository.findAllByApproved(true, pr).map(modelMapper::fromModelToResponse),
//                                modelRepository.countByApproved(true),
//                                pr
//                        )
//
//                );
        return getModelsTitle(null, true, pageableBody, userId);
    }

    @Override
    public Flux<PageableResponse<RESPONSE>> getModelsApproved(String title, PageableBody pageableBody, String userId) {
        return getModelsTitle(title, true, pageableBody, userId);
    }

    @Override
    public Flux<PageableResponse<RESPONSE>> getAllModels(String title, PageableBody pageableBody, String userId) {
        final String newTitle = title == null ? "" : title.trim();
        return pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
                .then(pageableUtils.createPageRequest(pageableBody))
                .flatMapMany(pr -> pageableUtils.createPageableResponse(
                                modelRepository.findAllByTitleContainingIgnoreCase(newTitle, pr).map(modelMapper::fromModelToResponse),
                                modelRepository.countAllByTitleContainingIgnoreCase(newTitle),
                                pr
                        )
                );
    }

    @Override
    public Mono<RESPONSE> createModel(BODY body, String userId) {
        return userUtils.getUser("", userId)
                .flatMap(authUser -> {
                    MODEL model = modelMapper.fromBodyToModel(body);
                    model.setUserId(authUser.getId());
                    model.setApproved(false);
                    model.setUserDislikes(new ArrayList<>());
                    model.setUserLikes(new ArrayList<>());
                    model.setImages(body.getImages());
                    return modelRepository.save(model);
                }).map(modelMapper::fromModelToResponse);
    }

    @Override
    public Mono<RESPONSE> getModelById(Long id, String userId) {
        return userUtils.getUser("", userId)
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> getResponseGuard(authUser, model, !model.isApproved()))
                );
    }


    @Override
    public Mono<ResponseWithUserDto<RESPONSE>> getModelByIdWithUser(Long id, String userId) {
        return userUtils.getUser("", userId)
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> getModelGuardWithUser(authUser, model, !model.isApproved()))
                );

    }

    public Flux<PageableResponse<RESPONSE>> getModelsTitle(String title, boolean approved, PageableBody pageableBody, String userId) {

        final String newTitle = title == null ? "" : title.trim();

        return
                userUtils.getUser("", userId).flatMap(
                                u -> {
                                    if (!u.getRole().equals(Role.ROLE_ADMIN) && !approved) {
                                        return Mono.error(new PrivateRouteException());
                                    }
                                    return Mono.just(u);
                                }
                        )
                        .then(pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields))
                        .then(pageableUtils.createPageRequest(pageableBody))
                        .flatMapMany(pr -> pageableUtils.createPageableResponse(
                                modelRepository.findAllByTitleContainingIgnoreCaseAndApproved(newTitle, approved, pr).map(modelMapper::fromModelToResponse),
                                modelRepository.countAllByTitleContainingIgnoreCaseAndApproved(newTitle, approved),
                                pr
                        ));
    }

    @Override
    public Mono<ResponseWithUserLikesAndDislikes<RESPONSE>> getModelByIdWithUserLikesAndDislikes(Long id, String userId) {
        return userUtils.getUser("", userId)
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> getModelGuardWithLikesAndDislikes(authUser, model, !model.isApproved()))
                );
    }


    @Override
    public Flux<PageableResponse<RESPONSE>> getModelsTrainer(String title, Long trainerId, PageableBody pageableBody, String userId) {
        String newTitle = title == null ? "" : title.trim();
        return userUtils.existsTrainerOrAdmin("/exists", trainerId)
                .then(pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields))
                .then(pageableUtils.createPageRequest(pageableBody))
                .flatMapMany(pr -> userUtils.getUser("", userId)
                        .flatMapMany(authUser -> privateRoute(true, authUser, trainerId))
                        .thenMany(pageableUtils.createPageableResponse(
                                modelRepository.findAllByUserIdAndTitleContainingIgnoreCase(trainerId, newTitle, pr).map(modelMapper::fromModelToResponse),
                                modelRepository.countAllByUserIdAndTitleContainingIgnoreCase(trainerId, newTitle),
                                pr
                        )));
    }

    public Mono<Tuple2<RESPONSE, UserDto>> getModelByIdWithOwner(Long id, String userId) {
        return userUtils.getUser("", userId)
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> getResponseGuard(authUser, model, !model.isApproved()))
                        .map(response -> Tuples.of(response, authUser))
                );
    }
}
