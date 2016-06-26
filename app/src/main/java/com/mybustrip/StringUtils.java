package com.mybustrip;

/**
 * Created by bengthammarlund on 11/05/16.
 */
public class StringUtils {

    public static boolean isBlank(final String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isNotBlank(final String s) {
        return !isBlank(s);
    }

}
