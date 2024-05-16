package com.example.commonmodule.services.impl;


import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.dtos.generic.WithUser;
import com.example.commonmodule.dtos.response.ResponseWithUserDto;
import com.example.commonmodule.dtos.response.ResponseWithUserLikesAndDislikes;
import com.example.commonmodule.mappers.DtoMapper;
import com.example.commonmodule.models.TitleBody;
import com.example.commonmodule.repositories.TitleBodyRepository;
import com.example.commonmodule.services.TitleBodyService;
import com.example.commonmodule.utils.EntitiesUtils;
import com.example.commonmodule.utils.PageableUtilsCustom;
import com.example.commonmodule.utils.UserUtils;
import reactor.core.publisher.Mono;

import java.util.List;

public abstract class TitleBodyServiceImpl<MODEL extends TitleBody, BODY, RESPONSE extends WithUser,
        S extends TitleBodyRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>>
        extends ManyToOneUserServiceImpl<MODEL, BODY, RESPONSE, S, M>
        implements TitleBodyService<MODEL, BODY, RESPONSE, S, M> {


    protected final EntitiesUtils entitiesUtils;

    public TitleBodyServiceImpl(S modelRepository, M modelMapper, PageableUtilsCustom pageableUtils, UserUtils userUtils, String modelName, List<String> allowedSortingFields, EntitiesUtils entitiesUtils) {
        super(modelRepository, modelMapper, pageableUtils, userUtils, modelName, allowedSortingFields);
        this.entitiesUtils = entitiesUtils;
    }

    @Override
    public Mono<RESPONSE> reactToModel(Long id, String type, String userId) {
        return userUtils.getUser("", userId)
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> entitiesUtils.setReaction(model, authUser, type)
                                .flatMap(modelRepository::save)
                                .map(modelMapper::fromModelToResponse)
                        )


                );
    }

    @Override
    public Mono<ResponseWithUserLikesAndDislikes<RESPONSE>> getModelByIdWithUserLikesAndDislikes(Long id, String userId) {
        return userUtils.getUser("", userId)
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> getModelGuardWithLikesAndDislikes(authUser, model, true))
                );
    }

    public Mono<ResponseWithUserLikesAndDislikes<RESPONSE>> getModelGuardWithLikesAndDislikes(UserDto authUser, MODEL model, boolean guard) {
        return getModelGuardWithUser(authUser, model, guard)
                .zipWith(userUtils.getUsersByIdIn("/byIds", model.getUserLikes()).collectList())
                .zipWith(userUtils.getUsersByIdIn("/byIds", model.getUserDislikes()).collectList())
                .map(tuple -> {
                    ResponseWithUserDto<RESPONSE> responseWithUserDto = tuple.getT1().getT1();
                    List<UserDto> userLikes = tuple.getT1().getT2();
                    List<UserDto> userDislikes = tuple.getT2();
                    return ResponseWithUserLikesAndDislikes.<RESPONSE>builder()
                            .model(responseWithUserDto.getModel())
                            .user(responseWithUserDto.getUser())
                            .userLikes(userLikes)
                            .userDislikes(userDislikes)
                            .build();
                });
    }


}
