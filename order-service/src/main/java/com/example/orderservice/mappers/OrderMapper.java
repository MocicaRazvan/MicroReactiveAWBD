package com.example.orderservice.mappers;


import com.example.commonmodule.mappers.DtoMapper;
import com.example.orderservice.clients.TrainingClient;
import com.example.orderservice.dtos.OrderBody;
import com.example.orderservice.dtos.OrderResponse;
import com.example.orderservice.dtos.messaging.OrderMessage;
import com.example.orderservice.models.Order;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public abstract class OrderMapper extends DtoMapper<Order, OrderBody, OrderResponse> {

    @Autowired
    protected TrainingClient trainingClient;

    public abstract OrderMessage fromResponseToMessage(OrderResponse response);


//    public abstract Order fromBodyToModel(OrderBody body);
//
//    public abstract OrderResponse fromModelToResponse(Order order);

    @Override
    public Mono<Order> updateModelFromBody(OrderBody body, Order order) {

        return trainingClient.verifyMappingTrainings(body.getTrainings())
                .then(Mono.fromCallable(
                        () -> {
                            order.setPayed(body.isPayed());
                            order.setShippingAddress(body.getShippingAddress());
                            order.setTrainings(
                                    body.getTrainings()
                                            .stream().distinct().toList()
                            );
                            return order;
                        }
                ));
    }

}
