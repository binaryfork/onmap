package com.binaryfork.onmap.view.place;

import android.location.Address;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.binaryfork.onmap.BaseApplication;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.model.GeoSearchModel;
import com.binaryfork.onmap.view.mediaview.ClusterGridView;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PlaceActivity extends AppCompatActivity {

    public static final String ARG_LOCATION = "loc";

    @InjectView(R.id.gridView) ClusterGridView gridView;
    @InjectView(R.id.imageView) ImageView imageView;
    @InjectView(R.id.title) TextView title;
    private LatLng location;
    private String flag = "http://www.geonames.org/flags/x/";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        ButterKnife.inject(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gridView.setColumns(3);
        gridView.setupData(BaseApplication.getMediaMapPresenter().getLoadedMedia());
        RecyclerViewHeader header = (RecyclerViewHeader) findViewById(R.id.header);
        header.attachTo(gridView, true);
        double[] loc = getIntent().getDoubleArrayExtra(ARG_LOCATION);
        location = new LatLng(loc[0], loc[1]);
        setAddress();
    }

    private void setAddress() {
        Address geoAddress = GeoSearchModel.geoAddressByLocation(location);
        if (geoAddress == null)
            return;
        String shortAddress = geoAddress.getLocality() != null ? geoAddress.getLocality() : geoAddress.getAdminArea();
        shortAddress = shortAddress == null ? geoAddress.getCountryName() : shortAddress + ", " + geoAddress.getCountryName();
        getSupportActionBar().setTitle(shortAddress);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i <= geoAddress.getMaxAddressLineIndex(); i++) {
            if (stringBuilder.length() > 0) stringBuilder.append(", ");
            stringBuilder.append(geoAddress.getAddressLine(i));
        }
        title.setText(stringBuilder);
        String flagUrl = flag + geoAddress.getCountryCode().toLowerCase(Locale.ENGLISH) + ".gif";
        Picasso.with(getApplicationContext()).load(flagUrl).into(imageView);
    }

}
