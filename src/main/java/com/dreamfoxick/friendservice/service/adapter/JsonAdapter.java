package com.dreamfoxick.friendservice.service.adapter;

import com.dreamfoxick.friendservice.data.mongo.entities.FriendEntity;
import com.dreamfoxick.friendservice.data.mongo.entities.TrackedUserEntity;
import com.dreamfoxick.friendservice.data.mongo.entities.UserEntity;
import com.dreamfoxick.friendservice.service.vkclient.json.common.objects.UserJson;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface JsonAdapter {
    @LogMethodCall
    UserEntity toUser(@NonNull UserJson json);

    @LogMethodCall
    Mono<UserEntity> toUser(@NonNull Mono<UserJson> json);

    @LogMethodCall
    FriendEntity toFriend(@NonNull UserJson json);

    @LogMethodCall
    Mono<FriendEntity> toFriend(@NonNull Mono<UserJson> json);

    @LogMethodCall
    TrackedUserEntity toTracked(@NonNull UserJson json);

    @LogMethodCall
    Mono<TrackedUserEntity> toTracked(@NonNull Mono<UserJson> json);
}
