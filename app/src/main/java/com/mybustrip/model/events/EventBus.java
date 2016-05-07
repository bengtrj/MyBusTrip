package com.mybustrip.model.events;

/**
 * Created by bengthammarlund on 03/05/16.
 */
public interface EventBus {

    void register(Object object);
    void unregister(Object object);
    void post(Object event);

}
