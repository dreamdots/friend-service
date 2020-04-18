package com.dreamfoxick.friendservice.service.executor.scheduler;

import com.dreamfoxick.friendservice.service.executor.scheduler.impl.job.JobGroup;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import lombok.NonNull;
import org.quartz.SchedulerException;
import reactor.core.publisher.Mono;

import java.text.ParseException;

public interface ScheduledCaller {

    @LogMethodCall
    Mono<Boolean> isAlive() ;

    @LogMethodCall
    Mono<String> report();

    @LogMethodCall
    Mono<Void> pause();

    @LogMethodCall
    Mono<Void> resume();

    @LogMethodCall
    Mono<Void> updateCronExpr(@NonNull JobGroup group,
                              @NonNull String cronExpr) throws SchedulerException, ParseException;

    @LogMethodCall
    Mono<Void> pauseJob(@NonNull JobGroup group) throws SchedulerException;

    @LogMethodCall
    Mono<Void> resumeJob(@NonNull JobGroup group) throws SchedulerException;
}
