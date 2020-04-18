package com.dreamfoxick.friendservice.util.collection;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CollUtils {

    /**
     * Select all elements from var2 that are not contained in var1
     */
    public <T> List<T> selectRejected(final List<T> var1,
                                      final List<T> var2) {
        return var2.stream()
                .filter(f -> !var1.contains(f))
                .collect(Collectors.toList());
    }
}
