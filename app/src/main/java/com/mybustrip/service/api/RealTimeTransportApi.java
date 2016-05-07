package com.mybustrip.service.api;

import com.mybustrip.model.PublicStopListResponse;
import com.mybustrip.model.RealtimeRouteResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by bengthammarlund on 03/05/16.
 */
public interface RealTimeTransportApi {

    @GET("busstopinformation")
    Call<PublicStopListResponse> listAllStops(@Query("operator") String operator);

    @GET("realtimebusinformation")
    Call<RealtimeRouteResponse> getRealtimeInfo(@Query("stopid") String stopId);

}
