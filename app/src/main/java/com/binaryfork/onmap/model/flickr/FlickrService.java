package com.binaryfork.onmap.model.flickr;

import com.binaryfork.onmap.model.flickr.model.FlickrPhotos;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * https://www.flickr.com/services/api/flickr.photos.search.html
 */
public interface FlickrService {

    @GET("/rest/?method=flickr.photos.getRecent")
    rx.Observable<FlickrPhotos> getRecentPhotos();

    @GET("/rest/?method=flickr.photos.search")
    rx.Observable<FlickrPhotos> searchByLocation(
            @Query("min_taken_date") long min,
            @Query("max_upload_date") long max,
            @Query("radius") int radius,
            @Query("lat") double latitude,
            @Query("lon") double longitude);
}
