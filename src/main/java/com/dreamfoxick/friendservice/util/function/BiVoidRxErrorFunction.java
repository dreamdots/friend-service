package com.dreamfoxick.friendservice.util.function;

import org.quartz.SchedulerException;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@FunctionalInterface
public interface BiVoidRxErrorFunction<T, R> {

    Mono<Void> apply(T var1, R var2) throws SchedulerException, ParseException;
}
