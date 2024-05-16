package com.example.userservice.controllers;

import com.example.commonmodule.controllers.UserController;
import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.UserBody;
import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.enums.Role;
import com.example.commonmodule.hateos.CustomEntityModel;
import com.example.commonmodule.hateos.user.PageableUserAssembler;
import com.example.commonmodule.utils.RequestsUtils;
import com.example.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final PageableUserAssembler pageableUserAssembler;
    private final RequestsUtils requestsUtils;


    @Override
    @GetMapping(value = "/roles", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_NDJSON_VALUE})
    public Mono<ResponseEntity<List<Role>>> getRoles() {
        return Mono.just(ResponseEntity.ok(List.of(Role.values())));
    }

    @Override
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<UserDto>>> getUser(@PathVariable Long id) {
        return userService.getUser(id)
                .flatMap(u -> pageableUserAssembler.getItemAssembler().toModel(u))
                .map(ResponseEntity::ok);

    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<PageableResponse<CustomEntityModel<UserDto>>> getAllUsers(@Valid @RequestBody PageableBody pageableBody,
                                                                          @RequestParam(required = false) String email,
                                                                          @RequestParam(required = false) Set<Role> roles) {
        return userService.getAllUsers(pageableBody, email, roles)
                .flatMap(pageableUserAssembler::toModel);
    }

    @Override
    @PatchMapping(value = "/admin/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<UserDto>>> makeTrainer(@PathVariable Long id) {
        return userService.makeTrainer(id)
                .flatMap(u -> pageableUserAssembler.getItemAssembler().toModel(u))
                .map(ResponseEntity::ok);
    }

    @Override
    @PutMapping(value = "/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<UserDto>>> updateUser(@PathVariable Long id, @Valid @RequestBody UserBody userBody, ServerWebExchange exchange) {
        return userService.updateUser(id, userBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(u -> pageableUserAssembler.getItemAssembler().toModel(u))
                .map(ResponseEntity::ok);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping(value = "/exists/{userId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<Void>> existsUserByIdAndRoleIn(@PathVariable Long userId, @RequestParam(required = false) Set<Role> roles) {
        return userService.existsUserByIdAndRoleIn(userId, roles)
                .map((p) -> ResponseEntity.noContent().build());
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/byIds", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<CustomEntityModel<UserDto>> getUsersByIdIn(@RequestParam(required = false) List<Long> ids) {
        log.info("getUsersByIdIn: {}", ids);
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }
        return userService.getUsersByIdIn(ids)
                .flatMap(pageableUserAssembler.getItemAssembler()::toModel);
    }
}
