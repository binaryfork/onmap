package com.binaryfork.onmap.model.youtube.model;


import android.text.Spannable;

import com.binaryfork.onmap.model.ApiSource;
import com.binaryfork.onmap.model.Media;
import com.binaryfork.spanny.Spanny;

public class VideoItem implements Media {

    private String id;
    private RecordingDetails recordingDetails;
    private Snippet snippet;

    @Override
    public String getPhotoUrl() {
        return "https://i.ytimg.com/vi/" + id + "/hqdefault.jpg";
    }

    @Override
    public String getThumbnail() {
        return "https://i.ytimg.com/vi/" + id + "/hqdefault.jpg";
    }

    @Override
    public String getVideoUrl() {
        return "";
    }

    @Override
    public String getTitle() {
        return snippet.title;
    }

    @Override
    public String getUserpic() {
        return "";
    }

    @Override
    public String getSiteUrl() {
        return "";
    }

    @Override
    public double getLatitude() {
        return recordingDetails.location.latitude;
    }

    @Override
    public double getLongitude() {
        return recordingDetails.location.longitude;
    }

    @Override
    public boolean isVideo() {
        return true;
    }

    @Override
    public long getCreatedDate() {
        return 0;
    }

    @Override
    public Spannable getComments() {
        Spanny spanny = new Spanny(snippet.description);
        return spanny;
    }

    @Override public String getAdderss() {
        return null;
    }

    @Override public ApiSource getApiSource() {
        return ApiSource.YOUTUBE;
    }
}
