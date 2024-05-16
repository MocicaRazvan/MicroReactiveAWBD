package com.example.orderservice.services.impl;

import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.response.EntityCount;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.exceptions.action.IllegalActionException;
import com.example.commonmodule.exceptions.action.SubEntityNotOwner;
import com.example.commonmodule.services.impl.ManyToOneUserServiceImpl;
import com.example.commonmodule.utils.PageableUtilsCustom;
import com.example.commonmodule.utils.UserUtils;
import com.example.orderservice.clients.TrainingClient;
import com.example.orderservice.dtos.OrderBody;
import com.example.orderservice.dtos.OrderResponse;
import com.example.orderservice.dtos.PriceDto;
import com.example.orderservice.enums.MessageType;
import com.example.orderservice.enums.OrderType;
import com.example.orderservice.mappers.OrderMapper;
import com.example.orderservice.messaging.OrderMessageProducer;
import com.example.orderservice.models.Order;
import com.example.orderservice.repositories.OrderRepository;
import com.example.orderservice.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
public class OrderServiceImpl
        extends ManyToOneUserServiceImpl<Order, OrderBody, OrderResponse, OrderRepository, OrderMapper>
        implements OrderService {

    private final TrainingClient trainingClient;
    private final OrderMessageProducer orderMessageProducer;

    public OrderServiceImpl(OrderRepository modelRepository, OrderMapper modelMapper, PageableUtilsCustom pageableUtils, UserUtils userUtils, TrainingClient trainingClient, OrderMessageProducer orderMessageProducer) {
        super(modelRepository, modelMapper, pageableUtils, userUtils, "order", List.of("id", "userId", "createdAt"));
        this.trainingClient = trainingClient;
        this.orderMessageProducer = orderMessageProducer;
    }

    @Override
    public Flux<PageableResponse<OrderResponse>> getAllModels(PageableBody pageableBody, OrderType orderType) {
        return pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
                .then(pageableUtils.createPageRequest(pageableBody))
                .map(pr -> {
                    if (orderType == null || orderType == OrderType.ALL) {
                        return Tuples.of(pr, modelRepository.findAllBy(pr), modelRepository.count());
                    } else {
                        boolean payed = orderType == OrderType.PAYED;
                        return Tuples.of(pr, modelRepository.findAllByPayed(payed, pr), modelRepository.countAllByPayed(payed));
                    }
                })
                .flatMapMany(tuple -> pageableUtils.createPageableResponse(
                        tuple.getT2().map(modelMapper::fromModelToResponse),
                        tuple.getT3(),
                        tuple.getT1()));
    }

    @Override
    public Mono<OrderResponse> payOrder(Long id, PriceDto priceDto, String userId) {
        return getModel(id)
                .flatMap(order -> {
                    if (order.isPayed()) {
                        return Mono.error(new IllegalActionException("Order with id " + id + " is already payed!"));
                    }
                    return userUtils.getUser("", userId)
                            .flatMap(authUser -> {
                                if (!order.getUserId().equals(authUser.getId())) {
                                    return Mono.error(new SubEntityNotOwner(authUser.getId(), order.getUserId(), order.getId()));
                                }
                                return trainingClient.getTotalPrice(order.getTrainings())
                                        .flatMap(priceTotal -> {
                                            if (!priceTotal.getTotalPrice().equals(priceDto.getPrice())) {
                                                return Mono.error(new IllegalActionException("Expected " + priceTotal.getTotalPrice() + " ,but got " + priceDto.getPrice()));
                                            }
                                            order.setPayed(true);
                                            return modelRepository.save(order)
                                                    .map(modelMapper::fromModelToResponse)
                                                    .flatMap(orderResponse -> sendMessage(orderResponse, MessageType.ORDER_PAYED));

                                        });
                            });
                });
    }


    private Mono<OrderResponse> sendMessage(OrderResponse orderResponse, MessageType messageType) {
        return orderMessageProducer.sendReactiveMessage(
                        modelMapper.fromResponseToMessage(orderResponse)
                                .map(m -> {
                                    m.setMessageType(messageType);
                                    return m;
                                })
                ).doOnError(ex -> log.error("Error sending message for order creation", ex))
                .thenReturn(orderResponse);
    }

    @Override
    public Flux<PageableResponse<OrderResponse>> getModelsByUser(Long userId, PageableBody pageableBody, OrderType orderType, String authUserId) {
        return pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
                .then(userUtils.getUser("", authUserId))
                .flatMap(authUser -> privateRoute(true, authUser, userId))
                .then(pageableUtils.createPageRequest(pageableBody))
                .map(pr -> {
                    if (orderType == null || orderType == OrderType.ALL) {
                        return Tuples.of(pr, modelRepository.findAllByUserId(userId, pr), modelRepository.countAllByUserId(userId));
                    } else {
                        boolean payed = orderType == OrderType.PAYED;
                        return Tuples.of(pr, modelRepository.findAllByUserIdAndPayed(userId, payed, pr), modelRepository.countAllByUserIdAndPayed(userId, payed));
                    }
                }).flatMapMany(tuple -> pageableUtils.createPageableResponse(
                        tuple.getT2().map(modelMapper::fromModelToResponse),
                        tuple.getT3(),
                        tuple.getT1()));
    }

    @Override
    public Mono<OrderResponse> createOrder(OrderBody body, String userId) {
        return trainingClient.verifyMappingTrainings(body.getTrainings())
                .then(Mono.defer(() -> {
                    Order order = modelMapper.fromBodyToModel(body);
                    order.setUserId(Long.valueOf(userId));
                    order.setCreatedAt(LocalDateTime.now());
                    order.setUpdatedAt(LocalDateTime.now());
                    order.setPayed(false);
                    return modelRepository.save(order)
                            .map(modelMapper::fromModelToResponse)
                            .flatMap(orderResponse -> sendMessage(orderResponse, MessageType.ORDER_CREATE));
                }));
    }

    @Override
    public Mono<EntityCount> countOrdersByTrainingId(Long trainingId) {
        return modelRepository.countOrdersByTrainingId(trainingId)
                .map(EntityCount::new);
    }
}
