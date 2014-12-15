package com.binaryfork.onmap;

import android.support.v4.app.FragmentActivity;

import com.binaryfork.onmap.instagram.InstagramService;
import com.octo.android.robospice.SpiceManager;

public abstract class BaseActivity extends FragmentActivity {
    private SpiceManager spiceManager = new SpiceManager(InstagramService.class);

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }

}