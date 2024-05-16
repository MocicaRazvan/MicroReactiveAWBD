package com.example.trainingservice.services.impl;

import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.dtos.response.EntityCount;
import com.example.commonmodule.dtos.response.ResponseWithChildList;
import com.example.commonmodule.dtos.response.ResponseWithUserDto;
import com.example.commonmodule.exceptions.action.SubEntityUsed;
import com.example.commonmodule.exceptions.notFound.NotFoundEntity;
import com.example.commonmodule.services.impl.ApprovedServiceImpl;
import com.example.commonmodule.utils.EntitiesUtils;
import com.example.commonmodule.utils.PageableUtilsCustom;
import com.example.commonmodule.utils.UserUtils;
import com.example.trainingservice.clients.ExerciseClient;
import com.example.trainingservice.clients.OrderClient;
import com.example.trainingservice.dto.TrainingBody;
import com.example.trainingservice.dto.TrainingResponse;
import com.example.trainingservice.dto.TrainingResponseWithOrderCount;
import com.example.trainingservice.dto.exercises.ExerciseResponse;
import com.example.trainingservice.dto.orders.TotalPrice;
import com.example.trainingservice.mappers.TrainingMapper;
import com.example.trainingservice.models.Training;
import com.example.trainingservice.repositories.TrainingRepository;
import com.example.trainingservice.services.TrainingService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TrainingServiceImpl extends
        ApprovedServiceImpl<Training, TrainingBody, TrainingResponse, TrainingRepository, TrainingMapper>
        implements TrainingService {

    private final ExerciseClient exerciseClient;
    private final OrderClient orderClient;

    public TrainingServiceImpl(TrainingRepository modelRepository, TrainingMapper modelMapper, PageableUtilsCustom pageableUtils, UserUtils userUtils, EntitiesUtils entitiesUtils, ExerciseClient exerciseClient, OrderClient orderClient) {
        super(modelRepository, modelMapper, pageableUtils, userUtils, "training", List.of("id", "userId", "title", "createdAt", "price"), entitiesUtils);
        this.exerciseClient = exerciseClient;
        this.orderClient = orderClient;
    }


    @Override
    public Mono<ResponseWithChildList<TrainingResponse, ResponseWithUserDto<ExerciseResponse>>> getTrainingWithExercises(Long id, boolean approved) {
        return modelRepository.findByApprovedAndId(approved, id)
                .switchIfEmpty(Mono.error(new NotFoundEntity("training", id)))
                .flatMap(training -> exerciseClient.getExercisesByIdsIn(training.getExercises())
                        .map(resp -> new ResponseWithUserDto<>(resp.getModel().getContent(), resp.getUser()))
                        .collectList()
                        .map(comments -> new ResponseWithChildList<>(modelMapper.fromModelToResponse(training), comments))
                );
    }

    @Override
    public Flux<ResponseWithChildList<TrainingResponse, ResponseWithUserDto<ExerciseResponse>>> getTrainingsWithExercises(List<Long> ids, boolean approved) {
        return modelRepository.findAllByApprovedAndIdIn(approved, ids)
                .flatMap(training -> exerciseClient.getExercisesByIdsIn(training.getExercises())
                        .map(resp -> new ResponseWithUserDto<>(resp.getModel().getContent(), resp.getUser()))
                        .collectList()
                        .map(comments -> new ResponseWithChildList<>(modelMapper.fromModelToResponse(training), comments))
                );
    }

    @Override
    public Mono<TrainingResponse> createModel(TrainingBody body, String userId) {
        return modelMapper.updateModelFromBody(body, new Training())
                .flatMap(training -> {
                    training.setApproved(false);
                    training.setUserId(Long.valueOf(userId));
                    return modelRepository.save(training).map(modelMapper::fromModelToResponse);
                });
    }

    @Override
    public Mono<TrainingResponse> deleteModel(Long id, String userId) {
        return orderClient.getTrainingInOrdersCount(id.toString())
                .flatMap(count -> {
                    if (count.getCount() > 0) {
                        return Mono.error(new SubEntityUsed("training", id));
                    }
                    return super.deleteModel(id, userId);
                });
    }

    @Override
    public Mono<TotalPrice> getTotalPriceById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Ids list is empty"));
        }
        return modelRepository.sumPriceByIds(ids)
                .map(TotalPrice::new);
    }

    @Override
    public Mono<TrainingResponseWithOrderCount> getTrainingWithOrderCount(Long id, String userId) {
        return getModelByIdWithOwner(id, userId)
                .zipWith(getTrainingInOrdersCount(id))
                .map(tuple -> {
                    TrainingResponse exercise = tuple.getT1().getT1();
                    EntityCount count = tuple.getT2();
                    UserDto user = tuple.getT1().getT2();
                    return new TrainingResponseWithOrderCount()
                            .fromTrainingResponse(exercise, count.getCount(), user);
                });

    }


    @Override
    public Mono<EntityCount> getExerciseInTrainingsCount(Long exerciseId) {
        return modelRepository.countExerciseInTrainings(exerciseId)
                .map(EntityCount::new);
    }

    @Override
    public Mono<EntityCount> getTrainingInOrdersCount(Long trainingId) {
        return orderClient.getTrainingInOrdersCount(trainingId.toString());
    }

    @Override
    public Mono<Void> validIds(List<Long> ids) {
        return entitiesUtils.validIds(ids, modelRepository, modelName);
    }
}
