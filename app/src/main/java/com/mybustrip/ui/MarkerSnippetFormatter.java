package com.mybustrip.ui;

import com.mybustrip.model.PublicStop;
import com.mybustrip.model.RealtimeRoute;

/**
 * Created by bengthammarlund on 30/05/16.
 */
public class MarkerSnippetFormatter {

    public boolean isDue(RealtimeRoute route) {
        return route.getDueTime().equalsIgnoreCase("due");
    }

    public CharSequence getPlainSnippet(PublicStop publicStop) {
        StringBuilder builder = new StringBuilder();

        for (String route : publicStop.getRoutes()) {
            builder.append(route).append(", ");
        }
        builder.deleteCharAt(builder.length() - 2);

        return builder;
    }

}
