package com.binaryfork.onmap.instagram.services;


import com.binaryfork.onmap.instagram.model.MediaResponse;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MediaService {

    @GET("/media/{media_id}")
    MediaResponse media(
            @Path("media_id") String mediaId);

    @GET("/media/search")
    MediaResponse mediaSearch(
            @Query("lat") double latitude,
            @Query("lng") double longitude,
            @Query("DISTANCE") int distance);

    @GET("/media/search")
    rx.Observable<MediaResponse> mediaSearch(
            @Query("lat") double latitude,
            @Query("lng") double longitude,
            @Query("min_timestamp") long minTimestamp,
            @Query("max_timestamp") long maxTimestamp);

    @GET("/media/search")
    rx.Observable<MediaResponse> mediaSearch(
            @Query("lat") double latitude,
            @Query("lng") double longitude);
}
