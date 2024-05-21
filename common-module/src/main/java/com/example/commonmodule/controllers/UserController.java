package com.example.commonmodule.controllers;

import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.UserBody;
import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.enums.AuthProvider;
import com.example.commonmodule.enums.Role;
import com.example.commonmodule.hateos.CustomEntityModel;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface UserController {

    Mono<ResponseEntity<List<Role>>> getRoles();

    Mono<ResponseEntity<CustomEntityModel<UserDto>>> getUser(Long id);

    Flux<PageableResponse<CustomEntityModel<UserDto>>> getAllUsers(
            @Valid @RequestBody PageableBody pageableBody, @RequestParam(required = false) String email,
            @RequestParam(required = false) Set<Role> roles, @RequestParam(required = false) Set<AuthProvider> providers

    );

    Mono<ResponseEntity<CustomEntityModel<UserDto>>> makeTrainer(Long id);

    Mono<ResponseEntity<CustomEntityModel<UserDto>>> updateUser(Long id, @Valid @RequestBody UserBody userBody, ServerWebExchange exchange);

    Mono<ResponseEntity<Void>> existsUserByIdAndRoleIn(Long userId, @RequestParam Set<Role> roles);

    Flux<CustomEntityModel<UserDto>> getUsersByIdIn(@RequestParam List<Long> ids);
}
