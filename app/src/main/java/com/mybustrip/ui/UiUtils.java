package com.mybustrip.ui;

import android.content.res.Resources;

/**
 * Created by bengthammarlund on 13/06/16.
 */
public class UiUtils {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
