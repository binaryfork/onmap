package com.binaryfork.onmap.model.google;

import com.binaryfork.onmap.model.google.model.GeocodeResults;

import retrofit.http.GET;
import retrofit.http.Query;

public interface GoogleGeoService {
    @GET("/json")
    rx.Observable<GeocodeResults> mediaSearch(
            @Query("address") String query);
}
