package com.dreamfoxick.friendservice.service.vkclient;

import com.dreamfoxick.friendservice.data.mongo.entities.TokenEntity;
import com.dreamfoxick.friendservice.service.vkclient.json.common.objects.ScreenNameJson;
import com.dreamfoxick.friendservice.service.vkclient.json.common.objects.UserJson;
import com.dreamfoxick.friendservice.service.vkclient.json.common.objects.UserTokenJson;
import com.dreamfoxick.friendservice.util.annotation.CallPerSecond;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import lombok.NonNull;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

import static com.dreamfoxick.friendservice.service.vkclient.limiter.TokenType.GROUP;
import static com.dreamfoxick.friendservice.service.vkclient.limiter.TokenType.USER;

public interface VkAPI {
    @LogMethodCall
    Mono<UserTokenJson> getAccessToken(@NonNull String code);

    /**
     * GROUP_TOKEN
     * https://vk.com/dev/users.get
     *
     * @return Mono.just(array) or Mono.empty()
     * @throws com.dreamfoxick.friendservice.service.vkclient.json.error.objects.ApiException         - https://vk.com/dev/errors
     * @throws com.dreamfoxick.friendservice.service.vkclient.json.error.objects.InvalidJsonException - if cannot parse json
     */
    @LogMethodCall
    @CallPerSecond(type = GROUP)
    Mono<List<UserJson>> usersGet(@NonNull List<Integer> ids);

    /**
     * USER_TOKEN
     *
     * @return Mono.just(arraySize, array) or Mono.empty()
     * @throws com.dreamfoxick.friendservice.service.vkclient.json.error.objects.ApiException         - https://vk.com/dev/errors
     * @throws com.dreamfoxick.friendservice.service.vkclient.json.error.objects.InvalidJsonException - if cannot parse json
     */
    @LogMethodCall
    @CallPerSecond(type = USER)
    Mono<Tuple2<Integer, List<UserJson>>> friendsGet(@NonNull int ID,
                                                     @NonNull TokenEntity token);

    @LogMethodCall
    @CallPerSecond(type = USER)
    Mono<List<Tuple2<Integer, List<UserJson>>>> executeFriendsGet(@NonNull String code,
                                                                  @NonNull TokenEntity token);

    /**
     * GROUP_TOKEN
     * https://vk.com/dev/utils.resolveScreenName
     *
     * @return Mono.just(pojo) or Mono.empty()
     * @throws com.dreamfoxick.friendservice.service.vkclient.json.error.objects.ApiException         - https://vk.com/dev/errors
     * @throws com.dreamfoxick.friendservice.service.vkclient.json.error.objects.InvalidJsonException - if cannot parse json
     */
    @LogMethodCall
    @CallPerSecond(type = GROUP)
    Mono<ScreenNameJson> resolveScreenName(@NonNull String screenName);
}
