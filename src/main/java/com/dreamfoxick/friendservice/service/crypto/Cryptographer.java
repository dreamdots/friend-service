package com.dreamfoxick.friendservice.service.crypto;

import com.dreamfoxick.friendservice.data.mongo.entities.TokenEntity;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface Cryptographer {

    @LogMethodCall
    TokenEntity encode(@NonNull String input);

    @LogMethodCall
    String decode(@NonNull TokenEntity token);

    @LogMethodCall
    Mono<TokenEntity> encode(@NonNull Mono<String> input);

    @LogMethodCall
    Mono<String> decode(@NonNull Mono<TokenEntity> token);
}
