package com.example.orderservice.hateos;


import com.example.commonmodule.hateos.controllerMaybe.generics.ManyToOneUserReactiveLinkBuilder;
import com.example.orderservice.controllers.OrderController;
import com.example.orderservice.dtos.OrderBody;
import com.example.orderservice.dtos.OrderResponse;
import com.example.orderservice.dtos.PriceDto;
import com.example.orderservice.enums.OrderType;
import com.example.orderservice.mappers.OrderMapper;
import com.example.orderservice.models.Order;
import com.example.orderservice.repositories.OrderRepository;
import com.example.orderservice.services.OrderService;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import java.util.List;

public class OrderReactiveLinkBuilder extends ManyToOneUserReactiveLinkBuilder<Order, OrderBody, OrderResponse,
        OrderRepository, OrderMapper, OrderService, OrderController> {

    @Override
    public List<WebFluxLinkBuilder.WebFluxLink> createModelLinks(OrderResponse orderResponse, Class<OrderController> c) {
        List<WebFluxLinkBuilder.WebFluxLink> links = super.createModelLinks(orderResponse, c);
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getOrderTypes()).withRel("orderTypes"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getAllOrdersAdmin(null, OrderType.ALL)).withRel("getAllOrdersAdmin"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).payOrder(orderResponse.getId(), new PriceDto(10), null)).withRel("payOrder"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getOrdersByUser(orderResponse.getUserId(), null, OrderType.ALL, null)).withRel("getOrdersByUser"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).createOrder(new OrderBody(), null)).withRel("createOrder"));
        return links;
    }
}
