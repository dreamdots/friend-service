package com.dreamfoxick.friendservice.util.logging;

import com.dreamfoxick.friendservice.service.vkclient.json.error.objects.ApiException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@UtilityClass
public class LogUtils {

    public void logCurrentClassNameAndMethodName() {
        val stack = Thread.currentThread().getStackTrace();
        log.trace(String.format("Call: %s.%s", stack[2].getClassName(), stack[2].getMethodName()));
    }

    public void logCurrentMethodName() {
        log.trace(String.format("Call: %s", Thread.currentThread().getStackTrace()[2].getMethodName()));
    }

    public void logStackTrace(final Throwable t) {
        log.error(String.format("Error: \n%s", Arrays
                .stream(t.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"))));
    }

    public void logApiEx(final Throwable ex) {
        log.warn(((ApiException) ex).toString());
    }

    public void logRespEx(final Throwable ex) {
        log.warn(((WebClientResponseException) ex).toString());
    }
}
