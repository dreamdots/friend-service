package com.dreamfoxick.friendservice.util.function;

import org.quartz.SchedulerException;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface VoidRxErrorFunction<T> {

    Mono<Void> apply(T var1) throws SchedulerException;
}
