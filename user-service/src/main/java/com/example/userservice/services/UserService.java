package com.example.userservice.services;


import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.UserBody;
import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.enums.AuthProvider;
import com.example.commonmodule.enums.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface UserService {
    Mono<UserDto> getUser(Long id);

    Flux<PageableResponse<UserDto>> getAllUsers(PageableBody pageableBody, String email, Set<Role> roles, Set<AuthProvider> providers);

    Mono<UserDto> makeTrainer(Long id);

    Mono<UserDto> updateUser(Long id, UserBody userBody, String userId);

    Mono<Boolean> existsUserByIdAndRoleIn(Long userId, Set<Role> roles);

    Flux<UserDto> getUsersByIdIn(List<Long> ids);
}
