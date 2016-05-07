package com.mybustrip.model.events;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by bengthammarlund on 03/05/16.
 */
public class MainThreadEventBus extends Bus implements EventBus {

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MainThreadEventBus.super.post(event);
                }
            });
        }
    }
}
