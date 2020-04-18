package com.dreamfoxick.friendservice.service.extractor.impl;

import com.dreamfoxick.friendservice.service.extractor.IdentifierExtractor;
import com.dreamfoxick.friendservice.util.annotation.LogMethodCall;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;

import java.util.regex.Pattern;

import static java.lang.String.*;
import static java.lang.String.format;

@ToString
public class IdentifierExtractorImpl implements IdentifierExtractor {
    private static final String IDENTIFIER_REGEX = "[_0-9a-zA-Z]+";
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile(IDENTIFIER_REGEX);

    private static final Pattern ANY_LINK_PATTERN = Pattern.compile(format("(http[s]?://)?(vk.com/)?(id)?%s", IDENTIFIER_REGEX));

    private static final String ID_REGEX = "(?m:id)[0-9]+";
    private static final Pattern ID_PATTERN = Pattern.compile(ID_REGEX);

    private static final String LINK_WITHOUT_ID_REGEX = "vk.com/";
    private static final String LINK_REGEX = format("%s%s", LINK_WITHOUT_ID_REGEX, IDENTIFIER_REGEX);
    private static final Pattern LINK_PATTERN = Pattern.compile(LINK_REGEX);

    private static final String FULL_LINK_WITHOUT_ID_REGEX = "http[s]?://vk.com/";
    private static final String FULL_LINK_REGEX = format("%s%s", FULL_LINK_WITHOUT_ID_REGEX, IDENTIFIER_REGEX);
    private static final Pattern FULL_LINK_PATTERN = Pattern.compile(FULL_LINK_REGEX);

    @ToString.Include
    private String screenName;
    @ToString.Include
    private int ID;

    @ToString.Exclude
    private boolean isIntID;
    @ToString.Exclude
    private boolean isScreenName;

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public IdentifierExtractorImpl(@NonNull final String link) {
        if (link.isEmpty()) {
            throw new IllegalArgumentException("Link cannot be empty");
        }

        if (!ANY_LINK_PATTERN.matcher(link).matches()) {
            throw new IllegalArgumentException(format("Invalid link: %s", link));
        }

        val identifier = extractIdentifier(link);

        if (identifier.isEmpty()) {
            throw new IllegalArgumentException(format("Invalid identifier: %s", identifier));
        }

        while (true) {
            if (ID_PATTERN.matcher(identifier).matches()) {
                this.isIntID = true;
                this.isScreenName = false;
                this.screenName = null;
                this.ID = extractID(identifier);
                break;
            } else if (IDENTIFIER_PATTERN.matcher(identifier).matches()) {
                this.isIntID = false;
                this.isScreenName = true;
                this.screenName = extractScreenName(identifier);
                this.ID = 0;
                break;
            } else {
                throw new IllegalArgumentException(format("Invalid identifier: %s", identifier));
            }
        }
    }

    private String extractScreenName(final String identifier) {
        if (identifier.equals("id")) {
            throw new IllegalArgumentException(format("Invalid screen name: %s", identifier));
        }
        return identifier;
    }

    private int extractID(final String identifier) {
        int intID = Integer.parseInt(identifier.replaceAll("id", ""));

        if (intID == 0) {
            throw new IllegalArgumentException("ID cannot be 0");
        }

        return intID;
    }

    private String extractIdentifier(final String link) {
        if (FULL_LINK_PATTERN.matcher(link).matches()) {
            return link.replaceAll(FULL_LINK_WITHOUT_ID_REGEX, "");
        } else if (LINK_PATTERN.matcher(link).matches()) {
            return link.replaceAll(LINK_WITHOUT_ID_REGEX, "");
        } else {
            if (link.matches("[0-9]+")) {
                return format("id%s", link);
            } else return link;
        }
    }

    @Override
    @LogMethodCall
    public boolean isIntID() {
        return isIntID;
    }

    @Override
    @LogMethodCall
    public boolean isScreenName() {
        return isIntID;
    }

    @Override
    @LogMethodCall
    public int getID() {
        return ID;
    }

    @Override
    @LogMethodCall
    public String getScreenName() {
        return screenName;
    }
}
