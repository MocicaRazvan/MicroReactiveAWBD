package com.example.commonmodule.controllers;


import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.generic.WithUser;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.dtos.response.ResponseWithUserDtoEntity;
import com.example.commonmodule.hateos.CustomEntityModel;
import com.example.commonmodule.mappers.DtoMapper;
import com.example.commonmodule.models.ManyToOneUser;
import com.example.commonmodule.repositories.ManyToOneUserRepository;
import com.example.commonmodule.services.ManyToOneUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


//@RequiredArgsConstructor
public interface ManyToOneUserController<MODEL extends ManyToOneUser, BODY, RESPONSE extends WithUser,
        S extends ManyToOneUserRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>,
        G extends ManyToOneUserService<MODEL, BODY, RESPONSE, S, M>> {

    //    protected final G modelService;
//    protected final String modelName;
//    @Value("${wellness.openapi.dev-url}")
//    protected String devUrl;
    @DeleteMapping("/delete/{id}")
    Mono<ResponseEntity<CustomEntityModel<RESPONSE>>> deleteModel(@PathVariable Long id, ServerWebExchange exchange);

    @GetMapping("/{id}")
    Mono<ResponseEntity<CustomEntityModel<RESPONSE>>> getModelById(@PathVariable Long id, ServerWebExchange exchange);

    @GetMapping("/withUser/{id}")
    Mono<ResponseEntity<ResponseWithUserDtoEntity<RESPONSE>>> getModelByIdWithUser(@PathVariable Long id, ServerWebExchange exchange);

    @PutMapping("/update/{id}")
    Mono<ResponseEntity<CustomEntityModel<RESPONSE>>> updateModel(@Valid @RequestBody BODY body,
                                                                  @PathVariable Long id, ServerWebExchange exchange);

    @GetMapping("/byIds")
    @ResponseStatus(HttpStatus.OK)
    Flux<PageableResponse<CustomEntityModel<RESPONSE>>> getModelsByIdIn(@Valid @RequestBody PageableBody pageableBody,
                                                                        @RequestParam List<Long> ids);
}
