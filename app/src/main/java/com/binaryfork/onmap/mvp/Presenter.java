package com.binaryfork.onmap.mvp;

import android.location.Location;

public interface Presenter {

    void onLocationUpdate(Location location);
    void onDateChange(Location location, long from, long to);
    void onDestroy();

}
