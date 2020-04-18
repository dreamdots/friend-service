package com.dreamfoxick.friendservice.util.annotation;

import com.dreamfoxick.friendservice.service.vkclient.limiter.TokenType;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface CallPerSecond {

    TokenType type();
}
