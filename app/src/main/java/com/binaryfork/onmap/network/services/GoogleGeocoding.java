package com.binaryfork.onmap.network.services;

import com.binaryfork.onmap.network.model.GeocodeResults;

import retrofit.http.GET;
import retrofit.http.Query;

public interface GoogleGeocoding {
    @GET("/json")
    rx.Observable<GeocodeResults> mediaSearch(
            @Query("address") String query);
}
