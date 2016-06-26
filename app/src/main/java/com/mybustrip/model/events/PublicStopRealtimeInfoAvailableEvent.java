package com.mybustrip.model.events;

import com.mybustrip.model.RealtimeRoute;

import java.util.List;

/**
 * Created by bengthammarlund on 02/05/16.
 */
public class PublicStopRealtimeInfoAvailableEvent {

    public PublicStopRealtimeInfoAvailableEvent() {

    }

    public PublicStopRealtimeInfoAvailableEvent(String stopId, List<RealtimeRoute> realtimeRoutes) {
        this.stopId = stopId;
        this.realtimeRoutes = realtimeRoutes;
    }

    public List<RealtimeRoute> realtimeRoutes;
    public String stopId;

}
