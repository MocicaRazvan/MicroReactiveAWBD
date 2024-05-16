package com.example.invoicesservice.services.impl;

import com.example.invoicesservice.clients.ExerciseClient;
import com.example.invoicesservice.clients.TrainingClient;
import com.example.invoicesservice.clients.UserClient;
import com.example.invoicesservice.dtos.*;
import com.example.invoicesservice.dtos.order.OrderBase;
import com.example.invoicesservice.dtos.order.OrderDto;
import com.example.invoicesservice.dtos.order.OrderMessage;
import com.example.invoicesservice.mappers.ExerciseMapper;
import com.example.invoicesservice.mappers.OrderMapper;
import com.example.invoicesservice.mappers.TrainingMapper;
import com.example.invoicesservice.mappers.UserMapper;
import com.example.invoicesservice.models.Order;
import com.example.invoicesservice.models.Training;
import com.example.invoicesservice.repositories.OrderRepository;
import com.example.invoicesservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final UserClient userClient;
    private final TrainingClient trainingClient;
    private final ExerciseClient exerciseClient;
    private final ExerciseMapper exerciseMapper;
    private final TrainingMapper trainingMapper;
    private final Executor asyncExecutor;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    @Override
    public void createOrder(OrderMessage orderMessage) {
        CustomEntityModel<UserDto> CustomEntityUser = userClient.getUser(orderMessage.getUserId());
        List<CustomEntityModel<TrainingResponse>> CustomEntityTrainings = trainingClient.getTrainingsByIds(orderMessage.getTrainings());
        CustomEntityTrainings.stream().map(CustomEntityModel::getContent).forEach(trainingResponse -> {
            List<CustomEntityModel<ExerciseResponse>> exercises = exerciseClient.getExercisesByIds(trainingResponse.getExercises());
            log.error("Exercises: {}", exercises);
        });

        log.error("User: {}", CustomEntityUser.getContent());
        log.error("Trainings: {}", CustomEntityTrainings);
    }

    @Override
    @Transactional
    public void createOrderFuture(OrderMessage orderMessage) {
        CompletableFuture<CustomEntityModel<UserDto>> userFuture = fetchUser(orderMessage);

        CompletableFuture<List<CustomEntityModel<TrainingResponse>>> trainingsFuture = fetchTraining(orderMessage);

        CompletableFuture<List<TrainingsWithExercisesResponse>> trainingsWithExercises =
                fetchTrainingsWithExercises(trainingsFuture);


        userFuture.thenCombine(trainingsWithExercises, (user, trainings) -> {
            Order order = createOrder(orderMessage, user, trainings);
            log.error("Order: {}", order);
            Order savedOrder = orderRepository.save(order);

            log.error("saved: {}", savedOrder);


            return null;
        }).join();
        
    }

    private Order createOrder(OrderMessage orderMessage, CustomEntityModel<UserDto> user, List<TrainingsWithExercisesResponse> trainings) {
        Order order = orderMapper.fromMessageToModel(orderMessage);
        order.setUser(userMapper.fromResponseToModel(user.getContent()));
        order.setTrainings(
                trainings.stream().map(te -> {
                    Training training = trainingMapper.fromResponseToModel(te.getTraining());
                    training.setExercises(
                            te.getExercises().stream().map(exerciseMapper::fromResponseToModel).toList()
                    );
                    return training;
                }).toList()
        );
        return order;
    }


    private CompletableFuture<List<CustomEntityModel<ExerciseResponse>>> fetchExercises(
            CompletableFuture<List<CustomEntityModel<TrainingResponse>>> trainingsFuture) {
        return trainingsFuture.thenCompose(trainings -> {
            List<CompletableFuture<List<CustomEntityModel<ExerciseResponse>>>> futures = trainings.stream().map(training ->
                    CompletableFuture.supplyAsync(() ->
                            exerciseClient.getExercisesByIds(training.getContent().getExercises()), asyncExecutor)
            ).toList();

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futures.stream().flatMap(f -> f.join().stream()).toList());
        });
    }

    private CompletableFuture<List<TrainingsWithExercisesResponse>> fetchTrainingsWithExercises(
            CompletableFuture<List<CustomEntityModel<TrainingResponse>>> trainingsFuture) {
        return trainingsFuture.thenCompose(trainings -> {
            List<CompletableFuture<TrainingsWithExercisesResponse>> futures = trainings.stream().map(training ->
                    CompletableFuture.supplyAsync(() ->
                                    exerciseClient.getExercisesByIds(training.getContent().getExercises()), asyncExecutor)
                            .thenApply(exercises ->
                                    new TrainingsWithExercisesResponse(training.getContent(),
                                            exercises.stream().map(CustomEntityModel::getContent).toList())
                            )
            ).toList();

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futures.stream().map(CompletableFuture::join).toList());
        });
    }

    private CompletableFuture<List<CustomEntityModel<TrainingResponse>>> fetchTraining(OrderMessage orderMessage) {
        return CompletableFuture.supplyAsync(
                () -> trainingClient.getTrainingsByIds(orderMessage.getTrainings()), asyncExecutor);
    }


    private CompletableFuture<CustomEntityModel<UserDto>> fetchUser(OrderMessage orderMessage) {
        return CompletableFuture.supplyAsync(
                () -> userClient.getUser(orderMessage.getUserId()), asyncExecutor);
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream().map(orderMapper::fromModelToDtoWithTrainings).toList();
    }

    @Override
    public void payOrder(OrderMessage orderMessage) {
        orderRepository.findById(orderMessage.getId())
                .ifPresent(order -> {
                    order.setPayed(true);
                    orderRepository.save(order);
                });
    }


}
