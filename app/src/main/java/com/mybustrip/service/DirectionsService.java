package com.mybustrip.service;

import android.content.Intent;

import roboguice.service.RoboIntentService;

/**
 * Created by bengthammarlund on 12/05/16.
 */
public class DirectionsService extends RoboIntentService {

    public static final String LOG_TAG = DirectionsService.class.getSimpleName();

    public DirectionsService() {
        super(DirectionsService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
