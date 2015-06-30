package com.binaryfork.onmap.model.youtube;


import com.binaryfork.onmap.model.youtube.model.InstagramItems;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * https://instagram.com/developer/endpoints/media/#get_media_search
 */
public interface YouTubeService {

    String l = "https://www.googleapis.com/youtube/v3/search?locationRadius=1000m&part=snippet" +
            "&location=37.42307%2C-122.08427&type=video" +
            "&key={YOUR_API_KEY}";
    String i = "https://www.googleapis.com/youtube/v3/videos?" +
            "part=snippet%2CrecordingDetails&id=VBZpUQQW824%2ChjR6Ozh9fRs" +
            "&key=AIzaSyBfjiKltU3I1eutWOZPSSZucV7C6eAbE2Y";

    @GET("/search")
    rx.Observable<InstagramItems> search(
            @Query("part") String part,
            @Query("type") String type,
            @Query("location") String location,
            @Query("locationRadius") int radius,
            @Query("maxResults") int maxResults);

    @GET("/videos")
    rx.Observable<InstagramItems> videos(
            @Query("part") String part,
            @Query("id") String id);
}
