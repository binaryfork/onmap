package com.binaryfork.onmap.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.EditText;

import com.binaryfork.onmap.model.GeoSearchModel;
import com.binaryfork.onmap.model.Media;
import com.binaryfork.onmap.model.ModelImplementation;
import com.binaryfork.onmap.model.foursquare.model.FoursquareResponse;
import com.binaryfork.onmap.model.google.model.GeocodeItem;
import com.binaryfork.onmap.model.google.model.GeocodeResults;
import com.binaryfork.onmap.view.search.GeoSearchView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class SearchPresenterImplementation implements SearchPresenter {

    private GeoSearchView geoSearchView;
    private ArrayList<Media> popularPlaces = new ArrayList<>();
    private ArrayList<Media> searchPlaces = new ArrayList<>();
    private Context context;
    private LatLng location;

    public SearchPresenterImplementation(GeoSearchView geoSearchView, Context context) {
        this.geoSearchView = geoSearchView;
        this.context = context;
    }

    @Override public void suggestGeoLocations(EditText editText) {
        GeoSearchModel.subscribe(editText, new Action1<GeocodeResults>() {
            @Override public void call(GeocodeResults geocodeResults) {
                searchPlaces = new ArrayList<>();
                for (GeocodeItem item : geocodeResults.results) {
                    SearchItem searchItem = new SearchItem(
                            item.formatted_address,
                            context.getResources().getDrawable(android.R.drawable.ic_menu_recent_history),
                            item.geometry.location.lat,
                            item.geometry.location.lng);
                    searchPlaces.add(searchItem);
                }
                geoSearchView.showSuggestions(searchPlaces);
            }
        });
    }

    @Override public Media getFirstSuggestion() {
        if (searchPlaces != null && searchPlaces.size() > 0)
            return searchPlaces.get(0);
        return null;
    }

    @Override public void loadPopularPlaces(LatLng location) {
        if (popularPlaces != null && popularPlaces.size() > 0 && (location == null || location.equals(this.location))) {
            geoSearchView.showPopularPlaces(popularPlaces);
            return;
        }
        this.location = location;
        new ModelImplementation().foursquare(location)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<FoursquareResponse>() {
                    @Override public void call(FoursquareResponse foursquareResponse) {
                        popularPlaces = (ArrayList<Media>) foursquareResponse.getList();
                        Timber.i("size " + popularPlaces.size());
                        geoSearchView.showPopularPlaces(popularPlaces);
                    }
                }, onError());
    }

    private Action1<Throwable> onError() {
        return new Action1<Throwable>() {
            @Override public void call(Throwable throwable) {
                Timber.e(throwable, "Marker subscription error");
            }
        };
    }

    public class SearchItem implements Media {

        public String address;
        public double lat;
        public double lng;

        public SearchItem(String formatted_address, Drawable drawable, double lat, double lng) {
            address = formatted_address;
            this.lat = lat;
            this.lng = lng;
        }

        @Override public Spannable getComments() {
            return new SpannableString(address);
        }

        @Override public String getPhotoUrl() {
            return null;
        }

        @Override public String getThumbnail() {
            return null;
        }

        @Override public String getVideoUrl() {
            return null;
        }

        @Override public String getTitle() {
            return null;
        }

        @Override public String getUserpic() {
            return null;
        }

        @Override public String getSiteUrl() {
            return null;
        }

        @Override public double getLatitude() {
            return lat;
        }

        @Override public double getLongitude() {
            return lng;
        }

        @Override public boolean isVideo() {
            return false;
        }

        @Override public long getCreatedDate() {
            return 0;
        }

        @Override public String getAdderss() {
            return null;
        }
    }
}
