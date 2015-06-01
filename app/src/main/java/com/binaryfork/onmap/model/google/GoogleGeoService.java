package com.binaryfork.onmap.model.google;

import com.binaryfork.onmap.model.google.model.GeocodeResults;

import retrofit.http.GET;
import retrofit.http.Query;

public interface GoogleGeoService {
    @GET("/json") rx.Observable<GeocodeResults> locationByAddress(
            @Query("address") String query);

    @GET("/json") rx.Observable<GeocodeResults> addressByLocation(
            @Query("address") String latlong);
}
