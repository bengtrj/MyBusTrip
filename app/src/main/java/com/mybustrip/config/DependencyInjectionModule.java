package com.mybustrip.config;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.mybustrip.MyApplication;
import com.mybustrip.service.api.GoogleMapsApi;
import com.mybustrip.service.api.RealTimeTransportApi;
import com.mybustrip.service.persistence.Repository;
import com.mybustrip.model.events.MainThreadEventBus;
import com.squareup.otto.Bus;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by bengthammarlund on 03/05/16.
 */
public class DependencyInjectionModule implements Module {

    public static final String RTPI_URL = "https://data.dublinked.ie/cgi-bin/rtpi/";
    public static final String GOOGLE_MAPS_API_URL = "https://maps.googleapis.com/maps/api/";


    @Override
    public void configure(Binder binder) {
        binder.bind(Bus.class).to(MainThreadEventBus.class).asEagerSingleton();
        binder.bind(Repository.class).toInstance(new Repository(MyApplication.getAppContext()));
        binder.bind(RealTimeTransportApi.class).toInstance(createRealtimeTransportApi());
        binder.bind(GoogleMapsApi.class).toInstance(createGoogleMapsApi());
    }

    private RealTimeTransportApi createRealtimeTransportApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RTPI_URL)
                .addConverterFactory(JacksonConverterFactory.create())
//                .client(new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .build();

        return retrofit.create(RealTimeTransportApi.class);
    }

    private GoogleMapsApi createGoogleMapsApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GOOGLE_MAPS_API_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(GoogleMapsApi.class);
    }


}
