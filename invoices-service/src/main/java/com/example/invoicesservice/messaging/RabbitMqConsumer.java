package com.example.invoicesservice.messaging;


import com.example.invoicesservice.dtos.order.OrderMessage;
import com.example.invoicesservice.enums.MessageType;
import com.example.invoicesservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqConsumer {

    private final OrderService orderService;

    @RabbitListener(queues = "#{@environment['order.queue']}")
    public void consumeMessage(OrderMessage orderMessage) {

        switch (orderMessage.getMessageType()) {

            case MessageType.ORDER_CREATE: {
                orderService.createOrderFuture(orderMessage);
                break;
            }
            case MessageType.ORDER_PAYED: {
                orderService.payOrder(orderMessage);
                break;
            }
        }
    }
}
