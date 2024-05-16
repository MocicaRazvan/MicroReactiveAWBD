package com.example.commonmodule.utils;


import com.example.commonmodule.enums.Role;
import com.example.commonmodule.exceptions.notFound.NotFoundEntity;
import com.example.commonmodule.models.Approve;
import com.example.commonmodule.repositories.ApprovedRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class EntitiesUtilsMapping extends EntitiesUtils {
    private final UserUtils userUtils;

    public <M extends Approve, R extends ApprovedRepository<M>> Mono<Void> verifyMapping(
            R repo, List<Long> ids, String userPath, String name, boolean pub) {

        return userUtils.getUser(userPath)
                .flatMap(
                        authUser ->
                                Flux.fromIterable(ids)
                                        .flatMap(id ->
                                                repo.findById(id)
                                                        .switchIfEmpty(Mono.error(new NotFoundEntity(name, id)))
                                                        .flatMap(m -> {
                                                            if (!pub && !authUser.getRole().equals(Role.ROLE_ADMIN)) {
                                                                return checkSubEntityOwner(m, authUser);
                                                            }

                                                            if (!m.isApproved()) {
                                                                return checkApproved(m, name);
                                                            }
                                                            return Mono.empty();
                                                        })
                                        )
                                        .then()
                );
    }
}
