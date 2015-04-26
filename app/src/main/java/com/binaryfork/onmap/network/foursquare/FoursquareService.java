package com.binaryfork.onmap.network.foursquare;


import com.binaryfork.onmap.network.foursquare.model.FoursquareResponse;

import retrofit.http.GET;
import retrofit.http.Query;

public interface FoursquareService {
    @GET("/venues/explore")
    rx.Observable<FoursquareResponse> explore(
            @Query("ll") String latLong,
            @Query("limit") int limit,
            @Query("venuePhotos") int enablePhotos);
}
