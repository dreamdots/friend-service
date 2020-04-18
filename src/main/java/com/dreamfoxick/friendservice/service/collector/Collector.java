package com.dreamfoxick.friendservice.service.collector;

import com.dreamfoxick.friendservice.data.mongo.entities.UserEntity;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import reactor.core.publisher.Flux;

public interface Collector {

    @LogMethodCall
    Flux<UserEntity> updateStatus();

    @LogMethodCall
    Flux<UserEntity> collectFriends();
}
