package com.dreamfoxick.friendservice.data.exception;


import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Exception for mongo entities
 */
@Getter
@NoArgsConstructor
public class EntityAlreadyExists extends RuntimeException {

    private String message;

    public EntityAlreadyExists(String message) {
        super(message);
        this.message = message;
    }
}
