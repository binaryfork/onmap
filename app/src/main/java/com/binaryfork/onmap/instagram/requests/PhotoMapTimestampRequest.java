package com.binaryfork.onmap.instagram.requests;

import com.binaryfork.onmap.instagram.model.Media;
import com.binaryfork.onmap.instagram.services.MediaService;

public class PhotoMapTimestampRequest extends BaseRequest<Media.MediaResponse, MediaService> {

    private double latitude;
    private double longitude;
    private long min;
    private long max;

    public PhotoMapTimestampRequest(double latitude, double longitude, long min, long max) {
        super(Media.MediaResponse.class, MediaService.class);
        this.latitude = latitude;
        this.longitude = longitude;
        this.min = min;
        this.max = max;
    }

    @Override
    public Media.MediaResponse loadDataFromNetwork() {
        return getService().mediaSearch(latitude, longitude, min, max);
    }

    @Override
    public String getRequestCacheKey() {
        return String.valueOf(latitude) + String.valueOf(longitude) + String.valueOf(max);
    }
}