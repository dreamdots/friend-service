package com.dreamfoxick.friendservice.service.vkclient.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Method {
    USERS_GET("users.get"),
    FRIENDS_GET("friends.get"),
    RESOLVE_SCREEN_NAME("utils.resolveScreenName"),
    EXECUTE("execute");

    private final String method;
}
