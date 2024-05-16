package com.example.orderservice.repositories;


import com.example.commonmodule.repositories.ManyToOneUserRepository;
import com.example.orderservice.models.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository extends ManyToOneUserRepository<Order> {

    Flux<Order> findAllByPayed(boolean payed, PageRequest request);

    Flux<Order> findAllBy(PageRequest request);

    Flux<Order> findAllByUserIdAndPayed(Long userId, boolean payed, PageRequest request);

    Mono<Long> countAllByPayed(boolean payed);

    Mono<Long> countAllByUserId(Long userId);

    Mono<Long> countAllByUserIdAndPayed(Long userId, boolean payed);

    @Query("""
                select count(*) from order_custom o
                where :trainingId = any (o.trainings)
            """)
    Mono<Long> countOrdersByTrainingId(Long trainingId);


}
