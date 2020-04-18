package com.dreamfoxick.friendservice.util.exception;

import com.dreamfoxick.friendservice.service.vkclient.json.error.objects.ApiException;
import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@UtilityClass
public class ExUtils {
    private static final int TOKEN_EX_CODE = 5;
    private static final int PRIVATE_EX_CODE = 30;
    private static final int DELETED_EX_CODE = 18;
    private static final int TOO_MANY_EX_CODE = 6;

    public boolean isTooManyRequestEx(Throwable t) {
        return isApiEx(t) && ((ApiException) t).getCode() == TOO_MANY_EX_CODE;
    }

    public boolean isInvalidTokenEx(Throwable t) {
        return isApiEx(t) && ((ApiException) t).getCode() == TOKEN_EX_CODE;
    }

    public boolean isPrivatePageEx(Throwable t) {
        return isApiEx(t) && ((ApiException) t).getCode() == PRIVATE_EX_CODE;
    }

    public boolean isDeletedPageEx(Throwable t) {
        return isApiEx(t) && ((ApiException) t).getCode() == DELETED_EX_CODE;
    }

    public boolean isWebClientResponseEx(Throwable t) {
        return t instanceof WebClientResponseException;
    }

    public boolean isApiEx(Throwable t) {
        return t instanceof ApiException;
    }
}
