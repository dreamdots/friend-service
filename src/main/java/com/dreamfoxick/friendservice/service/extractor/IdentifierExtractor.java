package com.dreamfoxick.friendservice.service.extractor;

import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;

public interface IdentifierExtractor {

    @LogMethodCall
    boolean isIntID();

    @LogMethodCall
    boolean isScreenName();

    @LogMethodCall
    int getID();

    @LogMethodCall
    String getScreenName();
}
