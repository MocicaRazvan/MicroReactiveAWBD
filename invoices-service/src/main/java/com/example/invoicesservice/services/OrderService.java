package com.example.invoicesservice.services;

import com.example.invoicesservice.dtos.order.OrderDto;
import com.example.invoicesservice.dtos.order.OrderMessage;

import java.util.List;

public interface OrderService {

    void createOrder(OrderMessage orderMessage);

    void createOrderFuture(OrderMessage orderMessage);

    List<OrderDto> getAllOrders();

    void payOrder(OrderMessage orderMessage);
}
