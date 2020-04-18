package com.dreamfoxick.friendservice.data.mongo.service;

import com.dreamfoxick.friendservice.data.mongo.entities.TrackedUserEntity;
import com.dreamfoxick.friendservice.data.mongo.entities.UserEntity;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserDTO {
    Mono<UserEntity> findByID(int ID);

    Flux<UserEntity> findAll();

    Mono<UserEntity> add(@NonNull UserEntity userEntity);

    Mono<UserEntity> update(@NonNull UserEntity userEntity);

    Mono<Boolean> exists(int ID);

    Mono<@NonNull TrackedUserEntity> addTrackedLink(int ID,
                                                    @NonNull TrackedUserEntity trackedUserEntity);

    Mono<Boolean> remove(int ID);

    Mono<Boolean> removeTrackedLink(int ID,
                                    int trackedID);
}
