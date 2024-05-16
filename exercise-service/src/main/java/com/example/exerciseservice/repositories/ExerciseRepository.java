package com.example.exerciseservice.repositories;


import com.example.commonmodule.repositories.ApprovedRepository;
import com.example.commonmodule.repositories.CountIds;
import com.example.exerciseservice.models.Exercise;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ExerciseRepository extends ApprovedRepository<Exercise>, CountIds {


//    @Query(
//            """
//                                select count(*) from training t
//                                where :exerciseId = any (t.exercises)
//                    """
//    )
//    Mono<Long> countTrainingsByExerciseId(Long exerciseId);


    @Query(
            """
                               select count(*) from exercise e
                               where e.id IN (:ids) and e.approved = true
                    """
    )
    Mono<Long> countByIds(List<Long> ids);

//    @Query("""
//                       select e.* ,
//                        count(t.id) as training_count
//                       from exercise e left join training t on e.id = any (t.exercises)
//                       where e.id = :id
//                       group by e.id, muscle_groups, e.approved, e.body, e.title, e.user_likes, e.user_dislikes, e.user_id, e.created_at, e.updated_at, e.images, videos
//
//            """)
//    Mono<ExerciseWithTrainingCount> findByIdWithTrainingCount(Long id);

    Flux<Exercise> findAllByUserIdAndApprovedTrue(Long userId);


}
