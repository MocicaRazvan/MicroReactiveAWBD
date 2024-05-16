package com.example.commonmodule.controllers;


import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.generic.TitleBody;
import com.example.commonmodule.dtos.generic.WithUser;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.hateos.CustomEntityModel;
import com.example.commonmodule.mappers.DtoMapper;
import com.example.commonmodule.models.Approve;
import com.example.commonmodule.repositories.ApprovedRepository;
import com.example.commonmodule.services.ApprovedService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApproveController<MODEL extends Approve, BODY extends TitleBody, RESPONSE extends WithUser,
        S extends ApprovedRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>,
        G extends ApprovedService<MODEL, BODY, RESPONSE, S, M>>

        extends TitleBodyController<MODEL, BODY, RESPONSE, S, M, G> {


    @PatchMapping("/approved")
    @ResponseStatus(HttpStatus.OK)
    Flux<PageableResponse<CustomEntityModel<RESPONSE>>> getModelsApproved(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody, ServerWebExchange exchange);

    @PatchMapping("/trainer/{trainerId}")
    @ResponseStatus(HttpStatus.OK)
    Flux<PageableResponse<CustomEntityModel<RESPONSE>>> getModelsTrainer(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody, @PathVariable Long trainerId, ServerWebExchange exchange);

    @PostMapping("/create")
    Mono<ResponseEntity<CustomEntityModel<RESPONSE>>> createModel(@Valid @RequestBody BODY body, ServerWebExchange exchange);

    @PatchMapping("/admin/approve/{id}")
    Mono<ResponseEntity<CustomEntityModel<RESPONSE>>> approveModel(@PathVariable Long id, ServerWebExchange exchange);

    @PatchMapping("/admin")
    Flux<PageableResponse<CustomEntityModel<RESPONSE>>> getAllModelsAdmin(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody, ServerWebExchange exchange);

}
