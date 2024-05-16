package com.example.userservice.services.impl;


import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.UserBody;
import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.enums.Role;
import com.example.commonmodule.exceptions.action.IllegalActionException;
import com.example.commonmodule.exceptions.action.PrivateRouteException;
import com.example.commonmodule.exceptions.notFound.NotFoundEntity;
import com.example.commonmodule.utils.EntitiesUtils;
import com.example.commonmodule.utils.PageableUtilsCustom;
import com.example.userservice.mappers.UserMapper;
import com.example.userservice.models.UserCustom;
import com.example.userservice.repositories.UserRepository;
import com.example.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final List<String> allowedSortingFields = List.of("firstName", "lastName", "email");

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final EntitiesUtils entitiesUtils;
    private final PageableUtilsCustom pageableUtilsCustom;

    @Override
    public Mono<UserDto> getUser(Long id) {
        return entitiesUtils.getEntityById(id, "user", userRepository)
                .map(userMapper::fromUserCustomToUserDto);
    }

    @Override
    public Flux<PageableResponse<UserDto>> getAllUsers(PageableBody pageableBody, String email, Set<Role> roles) {

        final String emailToSearch = email == null ? "" : email;

        if (roles == null) {
            roles = new HashSet<>();
        }

        if (roles.isEmpty()) {
            roles.add(Role.ROLE_USER);
            roles.add(Role.ROLE_ADMIN);
            roles.add(Role.ROLE_TRAINER);
        }
        log.info(pageableBody.getSortingCriteria().toString());
        log.info(allowedSortingFields.toString());

        final Set<Role> finalRoles = roles;
        return pageableUtilsCustom.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
                .then(pageableUtilsCustom.createPageRequest(pageableBody))
                .flatMapMany(pr -> pageableUtilsCustom.createPageableResponse(userRepository.findAllByEmailContainingIgnoreCaseAndRoleIn(emailToSearch, finalRoles, pr).map(userMapper::fromUserCustomToUserDto),
                        userRepository.countAllByEmailContainingIgnoreCaseAndRoleIn(emailToSearch, finalRoles), pr)
                );


    }


    @Override
    public Mono<UserDto> makeTrainer(Long id) {
        return entitiesUtils.getEntityById(id, "user", userRepository)
                .flatMap(user -> {
                    if (user.getRole().equals(Role.ROLE_ADMIN)) {
                        return Mono.error(new IllegalActionException("User is admin!"));
                    } else if (user.getRole().equals(Role.ROLE_TRAINER)) {
                        return Mono.error(new IllegalActionException("User is trainer!"));
                    }
                    user.setRole(Role.ROLE_TRAINER);
                    return userRepository.save(user).map(userMapper::fromUserCustomToUserDto);
                });
    }

    @Override
    public Mono<UserDto> updateUser(Long id, UserBody userBody, String userId) {
        return entitiesUtils.getEntityById(id, "user", userRepository)
                .zipWith(getAuthUser(Long.parseLong(userId)))
                .flatMap(tuple -> {
                    UserCustom user = tuple.getT1();
                    UserCustom authUser = tuple.getT2();

                    if (!user.getId().equals(authUser.getId())) {
                        return Mono.error(new PrivateRouteException());
                    }
                    user.setLastName(userBody.getLastName());
                    user.setFirstName(userBody.getFirstName());
                    user.setImage(userBody.getImage());
                    return userRepository.save(user).map(userMapper::fromUserCustomToUserDto);
                });

    }

    @Override
    public Mono<Boolean> existsUserByIdAndRoleIn(Long userId, Set<Role> roles) {
        return userRepository.existsByIdAndRoleIn(userId, roles)
                .filter(Boolean::booleanValue)
                .log()
                .switchIfEmpty(Mono.error(new NotFoundEntity("user", userId)));
    }

    @Override
    public Flux<UserDto> getUsersByIdIn(List<Long> ids) {
        return userRepository.findAllByIdIn(ids)
                .map(userMapper::fromUserCustomToUserDto);
    }

    public Mono<UserCustom> getAuthUser(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new NotFoundEntity("user", userId)));
    }
}
