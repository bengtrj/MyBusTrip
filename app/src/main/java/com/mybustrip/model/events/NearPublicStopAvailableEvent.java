package com.mybustrip.model.events;

import com.mybustrip.model.PublicStop;

/**
 * Created by bengthammarlund on 02/05/16.
 */
public class NearPublicStopAvailableEvent {

    public NearPublicStopAvailableEvent() {

    }

    public NearPublicStopAvailableEvent(PublicStop stop) {
        this.stop = stop;
    }

    public PublicStop stop;
}
