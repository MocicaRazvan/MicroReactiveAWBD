package com.example.orderservice.hateos;


import com.example.commonmodule.hateos.controllerMaybe.ReactiveResponseBuilder;
import com.example.orderservice.controllers.OrderController;
import com.example.orderservice.dtos.OrderResponse;
import org.springframework.stereotype.Component;

@Component
public class OrderReactiveResponseBuilder extends ReactiveResponseBuilder<OrderResponse, OrderController> {
    public OrderReactiveResponseBuilder() {
        super(new OrderReactiveLinkBuilder());
    }
}
