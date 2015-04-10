package com.binaryfork.onmap.network.flickr;

import com.binaryfork.onmap.network.flickr.model.FlickrPhotos;

import retrofit.http.GET;
import retrofit.http.Query;

public interface FlickrService {

    @GET("/rest/?method=flickr.photos.getRecent")
    public rx.Observable<FlickrPhotos> getRecentPhotos();

    @GET("/rest/?method=flickr.photos.search")
    public rx.Observable<FlickrPhotos> searchByLocation(
            @Query("lat") double latitude,
            @Query("lng") double longitude);
}
