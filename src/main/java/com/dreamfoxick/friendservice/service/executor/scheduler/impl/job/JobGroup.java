package com.dreamfoxick.friendservice.service.executor.scheduler.impl.job;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobGroup {
    FRIEND("fr"),
    UPDATE("up");

    private final String group;
}
