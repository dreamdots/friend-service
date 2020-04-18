package com.dreamfoxick.friendservice.data.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Exception for mongo entities
 */
@Getter
@NoArgsConstructor
public class EntityDoesNotExist extends RuntimeException {

    private String message;

    public EntityDoesNotExist(String message) {
        super(message);
        this.message = message;
    }
}
