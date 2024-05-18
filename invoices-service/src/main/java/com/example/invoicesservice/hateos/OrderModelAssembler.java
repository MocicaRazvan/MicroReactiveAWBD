package com.example.invoicesservice.hateos;


import com.example.invoicesservice.controllers.InvoiceController;
import com.example.invoicesservice.dtos.order.OrderDto;
import com.example.invoicesservice.models.Order;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<OrderDto, EntityModel<OrderDto>> {
    @Override
    public EntityModel<OrderDto> toModel(OrderDto entity) {
        EntityModel<OrderDto> orderEntityModel = EntityModel.of(entity);
        orderEntityModel.add(linkTo(methodOn(InvoiceController.class).getAllOrders()).withRel("all-orders").withType(HttpMethod.GET.name()));
        return orderEntityModel;
    }

    @Override
    public CollectionModel<EntityModel<OrderDto>> toCollectionModel(Iterable<? extends OrderDto> entities) {
        CollectionModel<EntityModel<OrderDto>> collectionModel = RepresentationModelAssembler.super.toCollectionModel(entities);
        collectionModel.add(linkTo(methodOn(InvoiceController.class).getAllOrders()).withRel("all-orders").withType(HttpMethod.GET.name()));
        return collectionModel;
    }
}
