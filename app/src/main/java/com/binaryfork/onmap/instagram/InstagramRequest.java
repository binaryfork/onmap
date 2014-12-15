package com.binaryfork.onmap.instagram;

import com.binaryfork.onmap.instagram.model.Media;
import com.binaryfork.onmap.instagram.services.MediaService;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

public class InstagramRequest extends RetrofitSpiceRequest<Media.MediaResponse, MediaService> {

    private double latitude;
    private double longitude;

    public InstagramRequest(double latitude, double longitude) {
        super(Media.MediaResponse.class, MediaService.class);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public Media.MediaResponse loadDataFromNetwork() {
        return getService().mediaSearch(latitude, longitude);
    }

    public String getRequestCacheKey() {
        return String.valueOf(latitude) + String.valueOf(longitude);
    }
}