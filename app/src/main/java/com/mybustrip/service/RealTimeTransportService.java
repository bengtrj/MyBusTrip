package com.mybustrip.service;

import android.content.Intent;
import android.util.Log;

import com.google.inject.Inject;
import com.mybustrip.config.Constants;
import com.mybustrip.model.RealtimeRoute;
import com.mybustrip.model.events.PublicStopRealtimeInfoAvailableEvent;
import com.mybustrip.service.api.RealTimeTransportApi;
import com.squareup.otto.Bus;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import roboguice.service.RoboIntentService;

/**
 * Created by bengthammarlund on 02/05/16.
 */
public class RealTimeTransportService extends RoboIntentService {

    public static final String RTPI_URL = "https://data.dublinked.ie/cgi-bin/rtpi/";
    public static final String LOG_TAG = RealTimeTransportService.class.getSimpleName();

    @Inject
    private Bus bus;

    private HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

    public RealTimeTransportService() {
        super("RealTimeTransportService");
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RTPI_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(interceptor).build())
                .build();

        final RealTimeTransportApi service = retrofit.create(RealTimeTransportApi.class);

        final String stopId = intent.getStringExtra(Constants.STOP_ID);

        if (stopId != null) {
            try {
                final List<RealtimeRoute> realtimeRoutes = service.getRealtimeInfo(stopId).execute().body().getResults();
                bus.post(new PublicStopRealtimeInfoAvailableEvent(stopId, realtimeRoutes));
            } catch (Exception e) {
                Log.w(LOG_TAG, "Erro ao carregar Realtime pra realtimeRoutes #" + stopId, e);
            }
        }
    }
}