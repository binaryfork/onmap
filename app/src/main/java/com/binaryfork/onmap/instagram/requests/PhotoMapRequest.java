package com.binaryfork.onmap.instagram.requests;

import com.binaryfork.onmap.instagram.model.Media;
import com.binaryfork.onmap.instagram.services.MediaService;

public class PhotoMapRequest extends BaseRequest<Media.MediaResponse, MediaService> {

    private double latitude;
    private double longitude;

    public PhotoMapRequest(double latitude, double longitude) {
        super(Media.MediaResponse.class, MediaService.class);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public Media.MediaResponse loadDataFromNetwork() {
        return getService().mediaSearch(latitude, longitude, 5000);
    }

    @Override
    public String getRequestCacheKey() {
        return String.valueOf(latitude) + String.valueOf(longitude);
    }
}