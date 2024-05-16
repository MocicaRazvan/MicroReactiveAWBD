package com.example.invoicesservice.controllers;

import com.example.invoicesservice.dtos.CustomEntityModel;
import com.example.invoicesservice.dtos.ExerciseResponse;
import com.example.invoicesservice.dtos.order.OrderDto;
import com.example.invoicesservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {


    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

}
