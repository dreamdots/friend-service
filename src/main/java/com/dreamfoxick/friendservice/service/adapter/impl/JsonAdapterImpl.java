package com.dreamfoxick.friendservice.service.adapter.impl;

import com.dreamfoxick.friendservice.data.mongo.entities.FriendEntity;
import com.dreamfoxick.friendservice.data.mongo.entities.TrackedUserEntity;
import com.dreamfoxick.friendservice.data.mongo.entities.UserEntity;
import com.dreamfoxick.friendservice.service.adapter.JsonAdapter;
import com.dreamfoxick.friendservice.service.vkclient.json.common.objects.UserJson;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import lombok.NonNull;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
@Profile({"dev", "test"})
public class JsonAdapterImpl implements JsonAdapter {

    @Override
    @LogMethodCall
    public UserEntity toUser(@NonNull final UserJson json) {
        return UserEntity.builder()
                .ID(json.getID())
                .firstName(json.getFirstName())
                .lastName(json.getLastName())
                .sex(json.getSex())
                .trackedUsers(new ArrayList<>())
                .token(null)
                .build();
    }

    @Override
    @LogMethodCall
    public Mono<UserEntity> toUser(@NonNull final Mono<UserJson> json) {
        return json.map(this::toUser);
    }

    @Override
    @LogMethodCall
    public FriendEntity toFriend(final UserJson json) {
        return FriendEntity.builder()
                .ID(json.getID())
                .firstName(json.getFirstName())
                .lastName(json.getLastName())
                .closed(json.isClosed())
                .sex(json.getSex())
                .deactivated(json.getDeactivated())
                .build();
    }

    @Override
    @LogMethodCall
    public Mono<FriendEntity> toFriend(@NonNull final Mono<UserJson> json) {
        return json.map(this::toFriend);
    }

    @Override
    @LogMethodCall
    public TrackedUserEntity toTracked(@NonNull final UserJson json) {
        return TrackedUserEntity.builder()
                .ID(json.getID())
                .firstName(json.getFirstName())
                .lastName(json.getLastName())
                .closed(json.isClosed())
                .sex(json.getSex())
                .deactivated(json.getDeactivated())
                .friends(new ArrayList<>())
                .build();
    }

    @Override
    @LogMethodCall
    public Mono<TrackedUserEntity> toTracked(@NonNull final Mono<UserJson> json) {
        return json.map(this::toTracked);
    }
}
