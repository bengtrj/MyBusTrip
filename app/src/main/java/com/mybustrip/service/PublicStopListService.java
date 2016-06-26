package com.mybustrip.service;

import android.content.Intent;
import android.location.Location;

import com.google.inject.Inject;
import com.mybustrip.R;
import com.mybustrip.config.Constants;
import com.mybustrip.model.PublicStop;
import com.mybustrip.model.events.NearPublicStopAvailableEvent;
import com.mybustrip.model.events.NearPublicStopDoneEvent;
import com.mybustrip.service.api.GoogleMapsApi;
import com.mybustrip.service.persistence.Repository;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.util.List;

import roboguice.inject.InjectResource;
import roboguice.service.RoboIntentService;

/**
 * Created by bengthammarlund on 02/05/16.
 */
public class PublicStopListService extends RoboIntentService implements DistanceFilter.DistanceMatchListener {

    private static final String LOG_TAG = PublicStopListService.class.getSimpleName();

    @InjectResource(R.string.google_places_key)
    private String apiKey;

    @Inject
    private Bus bus;

    @Inject
    private Repository repository;

    @Inject
    private PublicStopFinder publicStopFinder;

    @Inject
    GoogleMapsApi googleMapsApi;

    public PublicStopListService() {
        super(PublicStopListService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            final Location knownPosition = intent.getParcelableExtra(Constants.KNOW_POSITION);
            final List<PublicStop> knownPublicStops = publicStopFinder.find(knownPosition);
            new DistanceFilter().filter(this, knownPosition, knownPublicStops);
            if (publicStopFinder.getSourceOfData() == PublicStopFinder.Source.INTERNET) {
                saveToRepository(knownPublicStops);
            }
        } catch (IOException e) {
            //TODO Handle exception
            e.printStackTrace();
        }
    }

    @Override
    public void onMatch(PublicStop publicStop) {
        bus.post(new NearPublicStopAvailableEvent(publicStop));
    }

    @Override
    public void doneFiltering(List<PublicStop> filteredStops) {
        bus.post(new NearPublicStopDoneEvent(filteredStops.size()));
    }

    private void saveToRepository(final List<PublicStop> knownPublicStops) {
        for (PublicStop publicStop: knownPublicStops) {
            repository.save(publicStop);
        }
    }

//    public List<Place> getPlaces(Location location) {
//
//        try {
//
//            final String locationStr = location.getLatitude() + "," + location.getLongitude();
//
//            String language = Locale.getDefault().toString();
//            if (!language.equalsIgnoreCase("pt_BR")) {
//                language = "en_US";
//            }
//
//            Response<PlacesListResponse> response = googleMapsApi.listNearbyPlacesByType(locationStr, 5000, "bus_station", apiKey, language).execute();
//            Log.d(LOG_TAG, "HttpCode: " + response.code() + " - isSuccessful " + response.isSuccessful());
//            return response.body().getResults();
//        } catch (Exception e) {
//            throw new ApiException("Could not call Google Maps API", e);
//        }
//
//    }

}
