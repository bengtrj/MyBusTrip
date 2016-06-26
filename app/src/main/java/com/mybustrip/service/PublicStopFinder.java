package com.mybustrip.service;

import android.location.Location;
import android.util.Log;

import com.google.inject.Inject;
import com.mybustrip.CollectionUtil;
import com.mybustrip.model.PublicStop;
import com.mybustrip.service.api.RealTimeTransportApi;
import com.mybustrip.service.persistence.Repository;

import java.io.IOException;
import java.util.List;

/**
 * Created by bengthammarlund on 21/05/16.
 */
public class PublicStopFinder {

    private static final String LOG_TAG = PublicStopFinder.class.getSimpleName();

    public enum Source {
        CACHE, INTERNET, DATABASE
    };

    @Inject
    private Repository repository;

    @Inject
    RealTimeTransportApi realTimeTransportApi;

    private List<PublicStop> knownPublicStops;

    private Source sourceOfData;

    /**
     * Returns a String with the sourceOfData of the information
     * - Cache
     * - Database
     * - Internet
     */
    public List<PublicStop> find(final Location knownPosition) throws IOException {
        sourceOfData = Source.CACHE;
        final long initTime = System.currentTimeMillis();
        if (knownPublicStops == null) {
            findUsingDatabase(knownPosition);
            if (CollectionUtil.isEmpty(knownPublicStops)) {
                findUsingInternet();
            }
        }
        long elapsedTime = System.currentTimeMillis() - initTime;
        Log.i(LOG_TAG, "Took " + elapsedTime + " to get the list of all " + knownPublicStops.size() + " stops using " + sourceOfData);

        return knownPublicStops;
    }

    public Source getSourceOfData() {
        return sourceOfData;
    }

    private void findUsingInternet() throws IOException {
        knownPublicStops = realTimeTransportApi.listAllStops("bac").execute().body().getResults();
        sourceOfData = Source.INTERNET;
    }

    private void findUsingDatabase(Location knownPosition) {
        try {
            knownPublicStops = repository.listPublicStops(knownPosition.getLatitude(), knownPosition.getLongitude());
            if (CollectionUtil.isNotEmpty(knownPublicStops)) {
                sourceOfData = Source.DATABASE;
            }
        } catch (Exception e) {
            Log.w(LOG_TAG, "Error trying to list public stops from repository", e);
            throw new IllegalStateException(e);
        }
    }

}
