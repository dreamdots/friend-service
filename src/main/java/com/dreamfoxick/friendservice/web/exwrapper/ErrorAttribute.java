package com.dreamfoxick.friendservice.web.exwrapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * String values for exception response
 *
 * @see org.springframework.boot.web.reactive.error.DefaultErrorAttributes
 */
@RequiredArgsConstructor
public enum ErrorAttribute {
    STATUS("status"),
    ERROR("error");

    @Getter
    private final String value;
}
