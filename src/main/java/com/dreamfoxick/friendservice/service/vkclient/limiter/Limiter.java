package com.dreamfoxick.friendservice.service.vkclient.limiter;

import com.dreamfoxick.friendservice.configuration.YamlPropertySourceFactory;
import com.dreamfoxick.friendservice.data.mongo.entities.TokenEntity;
import com.dreamfoxick.friendservice.service.crypto.Cryptographer;
import com.dreamfoxick.friendservice.util.annotation.CallPerSecond;
import com.google.common.util.concurrent.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

import static com.dreamfoxick.friendservice.service.vkclient.limiter.TokenType.*;

@Slf4j
@Aspect
@Component
@Profile({"dev", "test"})
@SuppressWarnings("UnstableApiUsage")
@PropertySource(value = "classpath:limiter.yml", factory = YamlPropertySourceFactory.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class Limiter {
    private final Cryptographer cryptographer;

    private ConcurrentHashMap<String, RateLimiter> userLims = new ConcurrentHashMap<>();
    private RateLimiter groupLim;

    @Value("${group_request_per_second}")
    private int groupRequestPerSecond;
    @Value("${user_request_per_second}")
    private int userRequestPerSecond;

    @PostConstruct
    private void initialize() {
        groupLim = RateLimiter.create(groupRequestPerSecond);
    }

    @Pointcut("@within(com.dreamfoxick.friendservice.util.annotation.CallPerSecond) " +
            "|| @annotation(com.dreamfoxick.friendservice.util.annotation.CallPerSecond)")
    void point() {}

    @Around("point()")
    Object proceed(final ProceedingJoinPoint pjp) throws Throwable {
        val type = ((MethodSignature) pjp.getSignature())
                .getMethod()
                .getAnnotation(CallPerSecond.class)
                .type();
        return type.equals(GROUP)
                ? proceedGroupLim(pjp)
                : proceedUserLim(pjp);
    }

    private Object proceedGroupLim(final ProceedingJoinPoint pjp) throws Throwable {
        groupLim.acquire();
        return pjp.proceed();
    }

    private Object proceedUserLim(final ProceedingJoinPoint pjp) throws Throwable {
        TokenEntity tokenEntity = null;
        for (Object arg : pjp.getArgs()) {
            if (arg instanceof TokenEntity) {
                tokenEntity = (TokenEntity) arg;
            }
        }
        val token = cryptographer.decode(tokenEntity);
        if (!userLims.containsKey(token)) {
            userLims.put(token, RateLimiter.create(userRequestPerSecond));
        }
        userLims.get(token).acquire();
        return pjp.proceed();
    }
}
