package com.example.userservice.repositories;

import com.example.commonmodule.enums.AuthProvider;
import com.example.commonmodule.enums.Role;
import com.example.userservice.models.UserCustom;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends R2dbcRepository<UserCustom, Long> {

    Flux<UserCustom> findAllByEmailContainingIgnoreCaseAndRoleInAndProviderIn(String email, Set<Role> roles, Set<AuthProvider> providers, PageRequest pageRequest);

    Mono<Long> countAllByEmailContainingIgnoreCaseAndRoleInAndProviderIn(String email, Set<Role> roles, Set<AuthProvider> providers);

    Mono<UserCustom> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    @Query("SELECT COUNT(*) > 0 FROM user_custom u WHERE u.email = :email AND u.provider = 'LOCAL'")
    Mono<Boolean> existsLocalUser(String email);

    Mono<UserCustom> findByEmailAndProvider(String email, AuthProvider provider);

    @Query("SELECT COUNT(*) > 0 FROM user_custom u WHERE u.id = :userId AND u.role IN (:roles)")
    Mono<Boolean> existsByRoles(Long userId, List<Role> roles);

    Flux<UserCustom> findAllByIdIn(List<Long> ids);

    Mono<Boolean> existsByIdAndRoleIn(Long userId, Set<Role> roles);

}
