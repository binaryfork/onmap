package com.binaryfork.onmap.instagram.services;


import com.binaryfork.onmap.instagram.model.Media;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MediaService {

    @GET("/media/{media_id}")
    public void media(
            @Path("media_id") String mediaId,
            Callback<Media.MediaResponse> cb);

    @GET("/media/search")
    public Media.MediaResponse mediaSearch(
            @Query("lat") double latitude,
            @Query("lng") double longitude,
            @Query("DISTANCE") int distance);

    @GET("/media/search")
    public Media.MediaResponse mediaSearch(
            @Query("lat") double latitude,
            @Query("lng") double longitude,
            @Query("min_timestamp") long minTimestamp,
            @Query("max_timestamp") long maxTimestamp);

}
