package com.binaryfork.onmap.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.binaryfork.onmap.R;

public class MainPreferenceActivity extends PreferenceActivity {

    public static final String SAVE_LOCATION = "pref_save_location";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
