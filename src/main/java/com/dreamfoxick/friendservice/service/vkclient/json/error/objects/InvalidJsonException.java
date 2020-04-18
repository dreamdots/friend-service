package com.dreamfoxick.friendservice.service.vkclient.json.error.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InvalidJsonException extends RuntimeException {

    private String json;

    public InvalidJsonException(String json) {
        super(json);
        this.json = json;
    }
}
