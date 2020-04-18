package com.dreamfoxick.friendservice.data.mongo.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CollectionName {
    USERS("users");

    private final String value;
}
