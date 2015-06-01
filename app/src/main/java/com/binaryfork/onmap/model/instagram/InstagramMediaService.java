package com.binaryfork.onmap.model.instagram;


import com.binaryfork.onmap.model.instagram.model.InstagramItems;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * https://instagram.com/developer/endpoints/media/#get_media_search
 */
public interface InstagramMediaService {

    @GET("/media/{media_id}")
    InstagramItems media(
            @Path("media_id") String mediaId);

    @GET("/media/search")
    rx.Observable<InstagramItems> mediaSearch(
            @Query("lat") double latitude,
            @Query("lng") double longitude,
            @Query("min_timestamp") long minTimestamp,
            @Query("max_timestamp") long maxTimestamp,
            @Query("distance") int distance,
            @Query("count") int count);

    @GET("/media/search")
    rx.Observable<InstagramItems> mediaSearch(
            @Query("lat") double latitude,
            @Query("lng") double longitude,
            @Query("distance") int distance,
            @Query("count") int count);

    @GET("/media/popular")
    rx.Observable<InstagramItems> popular(
            @Query("count") int count);

    @GET("/tags/{tag}/media/recent")
    rx.Observable<InstagramItems> tagged(
            @Path("tag") String tag,
            @Query("count") int count);
}
