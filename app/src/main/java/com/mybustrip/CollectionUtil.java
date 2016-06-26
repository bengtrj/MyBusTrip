package com.mybustrip;

import java.util.Collection;

/**
 * Created by bengthammarlund on 21/05/16.
 */
public class CollectionUtil {

    public static boolean isEmpty(final Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(final Collection collection) {
        return !isEmpty(collection);
    }

}
