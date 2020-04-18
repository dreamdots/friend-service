package com.dreamfoxick.friendservice.util.function;

import org.quartz.SchedulerException;

@FunctionalInterface
public interface ErrorConsumer<T> {

    void apply(T var1) throws SchedulerException;
}
