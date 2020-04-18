package com.dreamfoxick.friendservice.service.notifier;

import com.dreamfoxick.friendservice.data.mongo.entities.FriendEntity;
import com.dreamfoxick.friendservice.data.mongo.entities.TrackedUserEntity;
import com.dreamfoxick.friendservice.data.mongo.entities.UserEntity;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface Notifier {

    @LogMethodCall
    Mono<Void> notifyOfRemovalFriend(@NonNull UserEntity user,
                                     @NonNull TrackedUserEntity trc,
                                     @NonNull FriendEntity friend);

    @LogMethodCall
    Mono<Void> notifyOfAdditionFriend(@NonNull UserEntity user,
                                      @NonNull TrackedUserEntity trc,
                                      @NonNull FriendEntity friend);

    @LogMethodCall
    Mono<Void> notifyOfDeletedPage(@NonNull UserEntity user,
                                   @NonNull TrackedUserEntity trc);

    @LogMethodCall
    Mono<Void> notifyOfPrivatePage(@NonNull UserEntity user,
                                   @NonNull TrackedUserEntity trc);

    @LogMethodCall
    Mono<Void> notifyOfWrongToken(@NonNull UserEntity user);
}
