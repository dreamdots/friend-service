package com.dreamfoxick.friendservice.web.controller;

import com.dreamfoxick.friendservice.service.executor.scheduler.ScheduledCaller;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Slf4j
@RestController
@Profile({"dev", "test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ActuatorController {
    private final ScheduledCaller actuator;

    @LogMethodCall
    @RequestMapping(value = "/actuator/alive", method = GET)
    public Mono<Boolean> alive() {
        return actuator.isAlive();
    }

    @LogMethodCall
    @RequestMapping(value = "/actuator/report", method = GET)
    public Mono<String> report() {
        return actuator.report();
    }

    @LogMethodCall
    @RequestMapping(value = "/actuator/pause", method = GET)
    public Mono<String> pause() {
        return actuator.pause()
                .then(Mono.just("Collector paused"));
    }

    @LogMethodCall
    @RequestMapping(value = "/actuator/resume", method = GET)
    public Mono<String> resume() {
        return actuator.resume()
                .then(Mono.just("Collector resumed"));
    }
}
