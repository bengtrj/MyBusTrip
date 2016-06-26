package com.mybustrip.model.events;

/**
 * Created by bengthammarlund on 02/05/16.
 */
public class NearPublicStopDoneEvent {

    public NearPublicStopDoneEvent() {

    }

    public NearPublicStopDoneEvent(int total) {
        this.total = total;
    }

    public int total;

}
