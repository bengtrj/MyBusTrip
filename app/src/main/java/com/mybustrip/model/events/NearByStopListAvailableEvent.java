package com.mybustrip.model.events;

import com.mybustrip.model.PublicStop;

import java.util.List;

/**
 * Created by bengthammarlund on 02/05/16.
 */
public class NearByStopListAvailableEvent {

    public NearByStopListAvailableEvent() {

    }

    public NearByStopListAvailableEvent(List<PublicStop> stops) {
        this.stops = stops;
    }

    public List<PublicStop> stops;
}
