package com.mybustrip.service.api;

import com.mybustrip.model.Place;
import com.mybustrip.model.PlacesListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by bengthammarlund on 02/05/16.
 */
public interface PlacesServiceApi {

    /**
     * Supported places:
     * https://developers.google.co/places/supported_types#table1
     */
    @GET("place/nearbysearch/json")
    Call<PlacesListResponse> listNearbyPlacesByType(@Query("location") String location,
                                                    @Query("radius") int radius,
                                                    @Query("type") String type,
                                                    @Query("key") String key,
                                                    @Query("language") String language);

}
