package com.mybustrip;

import com.mybustrip.config.DependencyInjectionModule;

import roboguice.RoboGuice;

/**
 * Created by bengthammarlund on 03/05/16.
 */
public class Application extends com.orm.SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();

        RoboGuice.overrideApplicationInjector(this,
                RoboGuice.newDefaultRoboModule(this),
                new DependencyInjectionModule());

    }
}
