package com.mybustrip.service;

import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.inject.Inject;
import com.mybustrip.R;
import com.mybustrip.config.Constants;
import com.mybustrip.model.PlacesListResponse;
import com.mybustrip.service.api.ApiException;
import com.mybustrip.service.api.PlacesServiceApi;
import com.mybustrip.service.api.RealTimeTransportApi;
import com.mybustrip.model.events.NearByStopListAvailableEvent;
import com.mybustrip.model.Place;
import com.mybustrip.model.PublicStop;
import com.orm.SugarRecord;
import com.orm.query.Select;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import roboguice.inject.InjectResource;
import roboguice.service.RoboIntentService;

/**
 * Created by bengthammarlund on 02/05/16.
 */
public class RealTimeTransportService extends RoboIntentService {

    public static final String RTPI_URL = "https://data.dublinked.ie/cgi-bin/rtpi/";
    public static final String URL_MAPS_API = "https://maps.googleapis.com/maps/api/";
    public static final String LOG_TAG = RealTimeTransportService.class.getSimpleName();

    @InjectResource(R.string.google_places_key)
    private String apiKey;

    @Inject
    private Bus bus;

    private List<PublicStop> knownPublicStops;

    private HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

    public RealTimeTransportService() {
        super("RealTimeTransportService");
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RTPI_URL)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(new OkHttpClient.Builder().addInterceptor(interceptor).build())
                    .build();

            final RealTimeTransportApi service = retrofit.create(RealTimeTransportApi.class);

            handlePublicStopCache(service);
            final Location knownPosition = intent.getParcelableExtra(Constants.KNOW_POSITION);
            bus.post(new NearByStopListAvailableEvent(filter(knownPosition, service)));
        } catch (IOException e) {
            //TODO Handle exception
            e.printStackTrace();
        }
    }

    private void handlePublicStopCache(final RealTimeTransportApi service) throws IOException {
        if (knownPublicStops == null) {
            try {
                knownPublicStops = Select.from(PublicStop.class).list();
            } catch (Exception e) {

            }
            if (knownPublicStops == null) {
                knownPublicStops = service.listAllStops("bac").execute().body().getResults();
                SugarRecord.saveInTx(knownPublicStops);
            }
        }
    }

    private List<PublicStop> filter(final Location location, final RealTimeTransportApi service) {

        final List<PublicStop> filtered = new ArrayList<>(10);
        int matched = 0;
        for (PublicStop stop : knownPublicStops) {
            if (stop.getDistance(location) < 0.50) {
                matched++;
                filtered.add(stop);
                try {
                    stop.setRealtimeRoutes(service.getRealtimeInfo(stop.getStopId()).execute().body().getResults());
                    Log.d(LOG_TAG, "");
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Erro ao carregar Realtime pra stop #" + stop.getStopId(), e);
                }
//                Place match = null;
//                for (Place nearbyPlace : places) {
//                    if (nearbyPlace.getDistance(stop) < 0.03) {
//                        filtered.add(stop);
//                    }
//                }
            }
        }
        Log.d(LOG_TAG, "Pre-filtered Stops: " + matched);
        Log.d(LOG_TAG, "Filtered Stops: " + filtered.size());
        return filtered;

    }

    public List<Place> getPlaces(Location location) {

        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL_MAPS_API)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();

            final String locationStr = location.getLatitude() + "," + location.getLongitude();
            final PlacesServiceApi service = retrofit.create(PlacesServiceApi.class);

            String language = Locale.getDefault().toString();
            if (!language.equalsIgnoreCase("pt_BR")) {
                language = "en_US";
            }

            Response<PlacesListResponse> response = service.listNearbyPlacesByType(locationStr, 5000, "bus_station", apiKey, language).execute();
            Log.d(LOG_TAG, "HttpCode: " + response.code() + " - isSuccessful " + response.isSuccessful());
            return response.body().getResults();
        } catch (Exception e) {
            throw new ApiException("Could not call " + URL_MAPS_API, e);
        }

    }



}
