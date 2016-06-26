package com.mybustrip.service.api;

import com.mybustrip.model.PlacesListResponse;
import com.mybustrip.service.api.model.Directions;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by bengthammarlund on 02/05/16.
 */
public interface GoogleMapsApi {

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

    /**
     * https://developers.google.com/maps/documentation/directions/intro#RequestParameters
     */
    @GET("directions/json")
    Call<Directions> getDirections(@Query("origin") String origin,
                                   @Query("destination") String destination,
                                   @Query("mode") String mode,
                                   @Query("transit_mode") String transit_mode,
                                   @Query("language") String language);


}
