package com.dreamfoxick.friendservice.service.collector.impl;

import com.dreamfoxick.friendservice.data.mongo.service.UserDTO;
import com.dreamfoxick.friendservice.data.mongo.entities.FriendEntity;
import com.dreamfoxick.friendservice.data.mongo.entities.TrackedUserEntity;
import com.dreamfoxick.friendservice.data.mongo.entities.UserEntity;
import com.dreamfoxick.friendservice.service.adapter.JsonAdapter;
import com.dreamfoxick.friendservice.service.collector.Collector;
import com.dreamfoxick.friendservice.service.notifier.Notifier;
import com.dreamfoxick.friendservice.service.vkclient.VkAPI;
import com.dreamfoxick.friendservice.service.vkclient.json.error.objects.ApiException;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import com.dreamfoxick.friendservice.util.collection.CollUtils;
import com.dreamfoxick.friendservice.util.exception.ExUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.*;

@Slf4j
@Component
@Profile({"dev", "test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CollectorImpl implements Collector {
    private static final ConcurrentHashMap<Integer, Integer> lookupFriendCounterMap = new ConcurrentHashMap<>();
    private final UserDTO userDTO;
    private final JsonAdapter adapter;
    private final Notifier notifier;
    private final VkAPI vkAPI;


    @Override
    @LogMethodCall
    public Flux<UserEntity> updateStatus() {
        return userDTO
                .findAll()
                .flatMap(user -> {
                    val updated = user.getTrackedUsers()
                            .stream()
                            .map(this::lookupTrackedUser)
                            .collect(Collectors.toList());
                    user.setTrackedUsers(updated);
                    return userDTO.update(user);
                });
    }

    /**
     * Lookup trc and record updates
     *
     * @throws ApiException                                                                vk error
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException request error
     */
    private TrackedUserEntity lookupTrackedUser(final TrackedUserEntity trc) {
        return vkAPI
                .usersGet(Collections.singletonList(trc.getID()))
                .map(jsons -> {
                    val updated = adapter.toTracked(jsons.get(0));
                    updated.setFriends(trc.getFriends());
                    return updated;
                })
                .block();
    }


    private boolean isVerifiable(final UserEntity user) {
        return user.getToken() != null
                && !user.getTrackedUsers().isEmpty();
    }

    private boolean isAvailable(final TrackedUserEntity trc) {
        return trc.getDeactivated() == null
                && !trc.isClosed();
    }

    @Override
    @LogMethodCall
    public Flux<UserEntity> collectFriends() {
        return userDTO
                .findAll()
                .filter(this::isVerifiable)
                .flatMap(user -> {
                    val updated = user.getTrackedUsers()
                            .stream()
                            .filter(this::isAvailable)
                            .map(trc -> lookupFriends(user, trc))
                            .collect(Collectors.toList());
                    user.setTrackedUsers(updated);
                    return userDTO.update(user);
                });
    }


    /**
     * Lookup friend and record updates
     *
     * @param user needed for notify and process token exception
     * @throws ApiException                                                                vk error
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException request error
     */
    private TrackedUserEntity lookupFriends(final UserEntity user,
                                            final TrackedUserEntity trc) {
        return vkAPI
                .friendsGet(trc.getID(), user.getToken())
                .defaultIfEmpty(Tuples.of(0, Collections.emptyList()))
                .map(t2 -> {
                    val oldFriends = trc.getFriends();
                    // actual friend list
                    val jsons = t2.getT2();
                    if (jsons.isEmpty()) trc.setFriends(oldFriends);
                    else {
                        // parse each to friend
                        val newFriends = jsons.stream()
                                .map(adapter::toFriend)
                                .collect(Collectors.toList());
                        if (oldFriends.isEmpty()) trc.setFriends(newFriends);
                        else computeUpdatedFriends(user, trc, oldFriends, newFriends);
                    }
                    return trc;
                })
                .onErrorResume(ExUtils::isApiEx,
                        ex -> proceedApiEx(user, trc, (ApiException) ex))
                .block();
    }

    private Mono<TrackedUserEntity> proceedApiEx(final UserEntity user,
                                                 final TrackedUserEntity trc,
                                                 final ApiException ex) {
        if (ExUtils.isInvalidTokenEx(ex)) {
            user.setToken(null);
            return notifier.notifyOfWrongToken(user).then(Mono.just(trc));
        } else if (ExUtils.isPrivatePageEx(ex)) {
            trc.setClosed(true);
            return notifier.notifyOfPrivatePage(user, trc).then(Mono.just(trc));
        } else if (ExUtils.isDeletedPageEx(ex)) {
            trc.setDeactivated("deleted");
            return notifier.notifyOfDeletedPage(user, trc).then(Mono.just(trc));
        } else return Mono.error(ex);
    }

    private void computeUpdatedFriends(
            final UserEntity user,
            final TrackedUserEntity trc,
            final List<FriendEntity> oldFriends,
            final List<FriendEntity> newFriends) {
        // case when a friend is added
        if (oldFriends.size() < newFriends.size()) {
            selectAddedFriendsAndAddThem(user, trc, oldFriends, newFriends);
            //case when a friend is deleted
        } else if (oldFriends.size() > newFriends.size()) {
            selectRemovableFriendsAndRemoveThem(user, trc, oldFriends, newFriends);
        } else {
            /*
            equality check collections because
            there may be a situation where the user
            deleted and added a friend in one time
             */
            boolean c = isEqualCollection(oldFriends, newFriends);
            if (!c) {
                selectAddedFriendsAndAddThem(user, trc, oldFriends, newFriends);
                selectRemovableFriendsAndRemoveThem(user, trc, oldFriends, newFriends);
            }
        }
    }

    private void selectRemovableFriendsAndRemoveThem(final UserEntity user,
                                                     final TrackedUserEntity trc,
                                                     final List<FriendEntity> oldFriends,
                                                     final List<FriendEntity> newFriends) {
        val removable = CollUtils.selectRejected(newFriends, oldFriends);
        removable.forEach(f -> {
            trc.getFriends().remove(f);
            notifier.notifyOfRemovalFriend(user, trc, f).subscribe();
        });
    }

    private void selectAddedFriendsAndAddThem(final UserEntity user,
                                              final TrackedUserEntity trc,
                                              final List<FriendEntity> oldFriends,
                                              final List<FriendEntity> newFriends) {
        val added = CollUtils.selectRejected(oldFriends, newFriends);
        added.forEach(f -> {
            trc.getFriends().add(f);
            notifier.notifyOfAdditionFriend(user, trc, f).subscribe();
        });
    }
}
