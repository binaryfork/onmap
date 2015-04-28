package com.binaryfork.onmap.network.foursquare;


import com.binaryfork.onmap.network.foursquare.model.FoursquareResponse;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * https://developer.foursquare.com/docs/venues/explore
 */
public interface FoursquareService {
    @GET("/venues/explore")
    rx.Observable<FoursquareResponse> explore(
            @Query("ll") String latLong,
            @Query("radius") int radius,
            @Query("limit") int limit,
            @Query("venuePhotos") int enablePhotos);
}
