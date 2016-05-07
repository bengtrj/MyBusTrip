package com.mybustrip.config;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.mybustrip.model.events.MainThreadEventBus;
import com.squareup.otto.Bus;

/**
 * Created by bengthammarlund on 03/05/16.
 */
public class DependencyInjectionModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(Bus.class).to(MainThreadEventBus.class).asEagerSingleton();
    }

}
