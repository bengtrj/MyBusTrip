package com.mybustrip.model;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bengthammarlund on 08/06/16.
 */
public class PublicStopMarkerManager {

    public enum MarkerStatus { CREATED, OBTAINING_REALTIME_DATA, REALTIME_DATA_AVAILABLE, REALTIME_DATA_UNAVAILABLE }

    private Map<PublicStop, Marker> markerByPublicStop = new HashMap<>();

    private Map<String, PublicStop> publicStopByMarkerId = new HashMap<>();

    private Map<String, PublicStop> publicStopsByStopId = new HashMap<>();

    private Map<String, MarkerStatus> markerStatusByMarkerId = new HashMap<>();

    public void put(PublicStop publicStop, Marker marker) {
        markerByPublicStop.put(publicStop, marker);
        publicStopsByStopId.put(publicStop.getStopId(), publicStop);
        publicStopByMarkerId.put(marker.getId(), publicStop);
        updateMarkerStatus(marker, MarkerStatus.CREATED);
    }

    public void updateMarkerStatus(Marker marker, MarkerStatus status) {
        markerStatusByMarkerId.put(marker.getId(), status);
    }

    public MarkerStatus getMarkerStatus(Marker marker) {
        return markerStatusByMarkerId.get(marker.getId());
    }

    public PublicStop getPublicStopByStopId(String stopId) {
        return publicStopsByStopId.get(stopId);
    }

    public PublicStop getPublicStopByMarkerId(String markerId) {
        return publicStopByMarkerId.get(markerId);
    }

    public Marker getMarker(PublicStop publicStop) {
        return markerByPublicStop.get(publicStop);
    }

}
