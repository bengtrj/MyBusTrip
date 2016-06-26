package com.mybustrip;

import android.content.Context;

import com.mybustrip.config.DependencyInjectionModule;

import roboguice.RoboGuice;

/**
 * Created by bengthammarlund on 03/05/16.
 */
public class MyApplication extends android.app.Application {

    private static final String LOG_TAG = MyApplication.class.getSimpleName();

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        RoboGuice.overrideApplicationInjector(this,
                RoboGuice.newDefaultRoboModule(this),
                new DependencyInjectionModule());

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static Context getAppContext() {
        return appContext;
    }

}
