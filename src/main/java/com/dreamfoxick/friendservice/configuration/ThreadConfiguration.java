package com.dreamfoxick.friendservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class ThreadConfiguration {

    @Bean
    public Scheduler blockingSch() {
        return Schedulers.newElastic("blocking_sch");
    }

    @Bean
    public Scheduler globalSch() {
        return Schedulers.newParallel("global_sch", Runtime.getRuntime().availableProcessors() * 2);
    }
}
