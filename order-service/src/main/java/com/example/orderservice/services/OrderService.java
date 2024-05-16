package com.example.orderservice.services;

import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.response.EntityCount;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.services.ManyToOneUserService;
import com.example.orderservice.dtos.OrderBody;
import com.example.orderservice.dtos.OrderResponse;
import com.example.orderservice.dtos.PriceDto;
import com.example.orderservice.enums.OrderType;
import com.example.orderservice.mappers.OrderMapper;
import com.example.orderservice.models.Order;
import com.example.orderservice.repositories.OrderRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService extends ManyToOneUserService<Order, OrderBody, OrderResponse, OrderRepository,
        OrderMapper> {

    Flux<PageableResponse<OrderResponse>> getAllModels(PageableBody pageableBody, OrderType orderType);

    Mono<OrderResponse> payOrder(Long id, PriceDto priceDto, String userId);

    Flux<PageableResponse<OrderResponse>> getModelsByUser(Long userId, PageableBody pageableBody, OrderType orderType, String authUserId);

    Mono<OrderResponse> createOrder(OrderBody body, String userId);

    Mono<EntityCount> countOrdersByTrainingId(Long trainingId);

}
