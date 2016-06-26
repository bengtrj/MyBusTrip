package com.mybustrip.service;

import android.location.Location;
import android.util.Log;

import com.mybustrip.model.PublicStop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bengthammarlund on 20/05/16.
 */
public class DistanceFilter {

    private static final String LOG_TAG = DistanceFilter.class.getSimpleName();

    public interface DistanceMatchListener {
        void onMatch(PublicStop publicStop);
        void doneFiltering(List<PublicStop> filteredStops);
    }

    public void filter(final DistanceMatchListener matchListener, final Location location, final List<PublicStop> knownPublicStops) {

        final List<PublicStop> filteredStops = new ArrayList<>();
        for (PublicStop stop : knownPublicStops) {
            if (isWithinRange(location, stop, 0.50)) {
                matchListener.onMatch(stop);
                filteredStops.add(stop);
            }
        }

        Log.i(LOG_TAG, "Filtered " + filteredStops.size() + " public stops");

        matchListener.doneFiltering(filteredStops);

    }

    private boolean isWithinRange(final Location location, final PublicStop stop, final double distanceInKm) {
        return stop.getDistanceInMeters(location) < distanceInKm;
    }

}
