package com.dreamfoxick.friendservice.util.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
class LogAspect {

    @Pointcut("@within(com.dreamfoxick.friendservice.util.annotation.LogMethodCall) " +
            "|| @annotation(com.dreamfoxick.friendservice.util.annotation.LogMethodCall)")
    void point(){}

    @Before("point()")
    void log(final JoinPoint jp) {
        log.trace(String.format("Execute: %s.%s",
                jp.getSignature().getDeclaringType().getSimpleName(),
                jp.getSignature().getName()));
    }
}
