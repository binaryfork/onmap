package com.binaryfork.onmap.instagram.services;


import com.binaryfork.onmap.instagram.model.Media;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MediaService {

    @GET("/media/{media_id}")
    public void media(
            @Path("media_id") String mediaId,
            Callback<Media.MediaResponse> cb);

    @GET("/media/search")
    public void mediaSearch(
            @Query("lat") double latitude,
            @Query("lng") double longitude,
            Callback<Media.MediaResponse> cb);

}
