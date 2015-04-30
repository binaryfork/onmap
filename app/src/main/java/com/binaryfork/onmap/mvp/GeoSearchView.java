package com.binaryfork.onmap.mvp;

public interface GeoSearchView {

    void setMapMediaView(MapMediaView mapMediaView);

    void showProgress(boolean isLoading);

    boolean isShown();

    void hide();
}
