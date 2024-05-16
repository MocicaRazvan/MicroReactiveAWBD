package com.example.commonmodule.utils;

import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.enums.Role;
import com.example.commonmodule.exceptions.action.IllegalActionException;
import com.example.commonmodule.exceptions.action.NotApprovedEntity;
import com.example.commonmodule.exceptions.action.SubEntityNotOwner;
import com.example.commonmodule.exceptions.notFound.NotFoundEntity;
import com.example.commonmodule.hateos.CustomEntityModel;
import com.example.commonmodule.models.Approve;
import com.example.commonmodule.models.ManyToOneUser;
import com.example.commonmodule.models.TitleBody;
import com.example.commonmodule.repositories.ApprovedRepository;
import com.example.commonmodule.repositories.CountIds;
import com.example.commonmodule.repositories.ManyToOneUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class EntitiesUtils {

    public <M extends ManyToOneUser, R extends ManyToOneUserRepository<M> & CountIds> Mono<Void> validIds(List<Long> ids, R modelRepository, String name) {
        return modelRepository.countByIds(ids)
                .map(count -> count == ids.size())
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new IllegalActionException(name + " " + ids.toString() + " are not valid")))
                .then();
    }

    public <M extends TitleBody> Mono<M> setReaction(M model, UserDto user, String type) {
        Set<Long> likes = new HashSet<>(model.getUserLikes());
        Set<Long> dislikes = new HashSet<>(model.getUserDislikes());

        if (type.equals("like")) {
            if (likes.contains(user.getId())) {
                likes.remove(user.getId());
            } else {
                likes.add(user.getId());
                dislikes.remove(user.getId());
            }
        } else if (type.equals("dislike")) {
            if (dislikes.contains(user.getId())) {
                dislikes.remove(user.getId());
            } else {
                dislikes.add(user.getId());
                likes.remove(user.getId());
            }
        }

        model.setUserLikes(likes.stream().toList());
        model.setUserDislikes(dislikes.stream().toList());
        return Mono.just(model);
    }

    public <T> Mono<T> getEntityById(Long id, String name, R2dbcRepository<T, Long> repository) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundEntity(name, id)));
    }


    public Mono<Void> checkSubEntityOwner(ManyToOneUser sub, UserDto user) {
        if (!sub.getUserId().equals(user.getId())) {
            return Mono.error(new SubEntityNotOwner(user.getId(), sub.getUserId(), sub.getId()));
        }
        return Mono.empty();
    }

    public Mono<Void> checkApproved(Approve entity, String name) {
        if (!entity.isApproved()) {
            return Mono.error(new NotApprovedEntity(name, entity.getId()));
        }
        return Mono.empty();
    }

}
