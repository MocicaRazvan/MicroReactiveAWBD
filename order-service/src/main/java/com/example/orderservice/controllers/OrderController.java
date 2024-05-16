package com.example.orderservice.controllers;

import com.example.commonmodule.controllers.ManyToOneUserController;
import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.response.EntityCount;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.dtos.response.ResponseWithUserDtoEntity;
import com.example.commonmodule.hateos.CustomEntityModel;
import com.example.commonmodule.utils.RequestsUtils;
import com.example.orderservice.dtos.OrderBody;
import com.example.orderservice.dtos.OrderResponse;
import com.example.orderservice.dtos.PriceDto;
import com.example.orderservice.enums.OrderType;
import com.example.orderservice.hateos.OrderReactiveResponseBuilder;
import com.example.orderservice.mappers.OrderMapper;
import com.example.orderservice.models.Order;
import com.example.orderservice.repositories.OrderRepository;
import com.example.orderservice.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController implements ManyToOneUserController<Order, OrderBody, OrderResponse,
        OrderRepository, OrderMapper, OrderService> {

    private final OrderService orderService;
    private final OrderReactiveResponseBuilder orderReactiveResponseBuilder;
    private final RequestsUtils requestsUtils;

    @Override
    @DeleteMapping(value = "/delete/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<OrderResponse>>> deleteModel(@PathVariable Long id, ServerWebExchange exchange) {
        return orderService.deleteModel(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(o -> orderReactiveResponseBuilder.toModel(o, OrderController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<OrderResponse>>> getModelById(@PathVariable Long id, ServerWebExchange exchange) {
        return orderService.getModelById(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(o -> orderReactiveResponseBuilder.toModel(o, OrderController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserDtoEntity<OrderResponse>>> getModelByIdWithUser(@PathVariable Long id, ServerWebExchange exchange) {
        return orderService.getModelByIdWithUser(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(o -> orderReactiveResponseBuilder.toModelWithUser(o, OrderController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PutMapping(value = "/update/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<OrderResponse>>> updateModel(@Valid @RequestBody OrderBody orderBody,
                                                                              @PathVariable Long id, ServerWebExchange exchange) {
        return orderService.updateModel(id, orderBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(o -> orderReactiveResponseBuilder.toModel(o, OrderController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/byIds", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<PageableResponse<CustomEntityModel<OrderResponse>>> getModelsByIdIn(@Valid @RequestBody PageableBody pageableBody,
                                                                                    @RequestParam List<Long> ids) {
        return orderService.getModelsByIdIn(ids, pageableBody)
                .flatMap(m -> orderReactiveResponseBuilder.toModelPageable(m, OrderController.class));
    }

    @GetMapping(value = "/types", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<List<OrderType>>> getOrderTypes() {
        return Mono.just(ResponseEntity.ok(List.of(OrderType.ALL, OrderType.PAYED, OrderType.NOT_PAYED)));
    }

    @PatchMapping(value = "/admin", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<OrderResponse>>> getAllOrdersAdmin(@Valid @RequestBody PageableBody pageableBody,
                                                                                      @RequestParam(required = false) OrderType orderType) {
        return orderService.getAllModels(pageableBody, orderType)
                .flatMap(m -> orderReactiveResponseBuilder.toModelPageable(m, OrderController.class));
    }

    @PatchMapping(value = "/pay/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<OrderResponse>>> payOrder(@PathVariable Long id, @Valid @RequestBody PriceDto priceDto, ServerWebExchange exchange) {
        return orderService.payOrder(id, priceDto, requestsUtils.extractAuthUser(exchange))
                .flatMap(o -> orderReactiveResponseBuilder.toModel(o, OrderController.class))
                .map(ResponseEntity::ok);
    }

    @PatchMapping(value = "/user/{userId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<PageableResponse<CustomEntityModel<OrderResponse>>> getOrdersByUser(
            @PathVariable Long userId,
            @Valid @RequestBody PageableBody pageableBody,
            @RequestParam(required = false) OrderType orderType, ServerWebExchange exchange
    ) {
        return orderService.getModelsByUser(userId, pageableBody, orderType, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> orderReactiveResponseBuilder.toModelPageable(m, OrderController.class));
    }

    @PostMapping(produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<OrderResponse>>> createOrder(@Valid @RequestBody OrderBody orderBody, ServerWebExchange exchange) {
        return orderService.createOrder(orderBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(o -> orderReactiveResponseBuilder.toModel(o, OrderController.class))
                .map(ResponseEntity::ok);
    }

    // todo "/internal/trainingCount/{trainingId}"
    @GetMapping(value = "/internal/trainingCount/{trainingId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<EntityCount>> getTrainingCount(@PathVariable Long trainingId) {
        return orderService.countOrdersByTrainingId(trainingId)
                .map(ResponseEntity::ok);
    }
}
