package com.example.orderservice.messaging;


import com.example.orderservice.dtos.messaging.OrderMessage;
import com.example.orderservice.exceptions.MessageSendingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class OrderMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${order.queue}")
    public String orderQueue;

    public Mono<Void> sendReactiveMessage(OrderMessage message) {
        return Mono.fromCallable(() -> {
                    rabbitTemplate.convertAndSend(orderQueue, message);
                    return null;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(ex -> new MessageSendingException("Failed to send order message", ex))
                .then();
    }
}
