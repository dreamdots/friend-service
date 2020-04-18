package com.dreamfoxick.friendservice.main;

import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;


/**
 * Cache
 * Actuator
 * Transactional
 * Security
 * Crypto
 * Cloud
 * Scheduler
 * Step verifier
 */
@EnableScheduling
@EnableWebFlux
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication(
        scanBasePackages = {
                "com.dreamfoxick.friendservice.web",
                "com.dreamfoxick.friendservice.main",
                "com.dreamfoxick.friendservice.configuration",
                "com.dreamfoxick.friendservice.service",
                "com.dreamfoxick.friendservice.data.mongo",
                "com.dreamfoxick.friendservice.util"
        }
)
public class FriendServiceApplication {
    public static void main(final String[] args) {
        SpringApplication.run(FriendServiceApplication.class, args);
    }
}
