package com.dreamfoxick.friendservice.web.exwrapper;

import com.dreamfoxick.friendservice.data.exception.EntityAlreadyExists;
import com.dreamfoxick.friendservice.data.exception.EntityDoesNotExist;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import lombok.val;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;

/**
 * Friends controller error wrapper. He processes exceptions and built http response.
 */
@Component
@Profile({"dev", "test"})
public class ErrorWrapper extends DefaultErrorAttributes {

    /**
     * @see DefaultErrorAttributes#getErrorAttributes(ServerRequest, boolean)
     */
    @Override
    @LogMethodCall
    public Map<String, Object> getErrorAttributes(final ServerRequest request,
                                                  final boolean includeStackTrace) {
        val error = getError(request);
        val defAttrs = super.getErrorAttributes(request, false);

        if (error instanceof IllegalArgumentException) {
            return proceedIllegalArgumentException((IllegalArgumentException) error, defAttrs);
        } else if (error instanceof EntityAlreadyExists) {
            return proceedEntityAlreadyExists((EntityAlreadyExists) error, defAttrs);
        } else if (error instanceof EntityDoesNotExist) {
            return proceedEntityDoesNotExists((EntityDoesNotExist) error, defAttrs);
        } else if (error instanceof IllegalStateException) {
            return proceedIllegalStateException(defAttrs);
        }

        return defAttrs;
    }

    private Map<String, Object> proceedIllegalStateException(final Map<String, Object> defAttrs) {
        defAttrs.replace(ErrorAttribute.STATUS.getValue(), HttpStatus.I_AM_A_TEAPOT.value());
        defAttrs.replace(ErrorAttribute.ERROR.getValue(), HttpStatus.I_AM_A_TEAPOT.getReasonPhrase());

        return defAttrs;
    }

    private Map<String, Object> proceedIllegalArgumentException(final IllegalArgumentException ex,
                                                                final Map<String, Object> defAttrs) {
        return proceedRuntime(computeStatus(ex, HttpStatus.BAD_REQUEST), defAttrs);
    }

    private Map<String, Object> proceedEntityAlreadyExists(final EntityAlreadyExists ex,
                                                           final Map<String, Object> defAttrs) {
        return proceedRuntime(computeStatus(ex, HttpStatus.BAD_REQUEST), defAttrs);
    }

    private Map<String, Object> proceedEntityDoesNotExists(final EntityDoesNotExist ex,
                                                           final Map<String, Object> defAttrs) {
        return proceedRuntime(computeStatus(ex, HttpStatus.BAD_REQUEST), defAttrs);
    }

    private Map<String, Object> proceedRuntime(final HttpStatus status,
                                               final Map<String, Object> defAttrs) {
        defAttrs.replace(ErrorAttribute.STATUS.getValue(), status.value());
        defAttrs.replace(ErrorAttribute.ERROR.getValue(), status.getReasonPhrase());

        return defAttrs;
    }

    /**
     * If controller method has {@link ResponseStatus} then returned annotation code value,
     * else returned custom exception code.
     *
     * @see DefaultErrorAttributes#getErrorAttributes(ServerRequest, boolean)
     * @see MergedAnnotations#from(AnnotatedElement, MergedAnnotations.SearchStrategy)
     */
    @SuppressWarnings("SameParameterValue")
    private HttpStatus computeStatus(final Throwable error,
                                     final HttpStatus defStatus) {
        return MergedAnnotations
                .from(error.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
                .get(ResponseStatus.class)
                .getValue("code")
                .map(HttpStatus.class::cast)
                .orElse(defStatus);
    }
}
