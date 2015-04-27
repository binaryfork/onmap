package com.binaryfork.onmap.activities;

import android.os.Bundle;

import com.binaryfork.onmap.mvp.MapMediaView;
import com.binaryfork.onmap.mvp.Presenter;
import com.binaryfork.onmap.mvp.PresenterImplementation;
import com.google.android.gms.maps.SupportMapFragment;

public class MapMediaFragment extends SupportMapFragment {

    private Presenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setMapMediaView(MapMediaView mapMediaView) {
        if (presenter == null)
            presenter = new PresenterImplementation();
        presenter.setMapMediaView(mapMediaView);
        presenter.setupClusterer(getActivity().getApplicationContext(), getMap());
    }

    public Presenter getPresenter() {
        return presenter;
    }

    @Override public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
