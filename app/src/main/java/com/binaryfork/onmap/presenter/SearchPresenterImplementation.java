package com.binaryfork.onmap.presenter;

import android.support.annotation.DrawableRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.EditText;

import com.binaryfork.onmap.Constants;
import com.binaryfork.onmap.model.GeoSearchModel;
import com.binaryfork.onmap.model.Media;
import com.binaryfork.onmap.model.MediaList;
import com.binaryfork.onmap.model.ModelImplementation;
import com.binaryfork.onmap.model.google.model.GeocodeItem;
import com.binaryfork.onmap.model.google.model.GeocodeResults;
import com.binaryfork.onmap.util.Theme;
import com.binaryfork.onmap.view.search.GeoSearchView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class SearchPresenterImplementation implements SearchPresenter {

    private GeoSearchView geoSearchView;
    private ArrayList<SearchItem> popularPlaces = new ArrayList<>();
    private ArrayList<SearchItem> searchPlaces = new ArrayList<>();
    private ArrayList<SearchItem> searchHistory = new ArrayList<>();
    private LatLng location;

    public SearchPresenterImplementation(GeoSearchView geoSearchView) {
        this.geoSearchView = geoSearchView;
    }

    @Override public void suggestGeoLocations(EditText editText) {
        GeoSearchModel.subscribe(editText, new Action1<GeocodeResults>() {
            @Override public void call(GeocodeResults geocodeResults) {
                searchPlaces = new ArrayList<>();
                for (GeocodeItem item : geocodeResults.results) {
                    searchPlaces.add(new SearchItem(item));
                }
                geoSearchView.showSuggestions(searchPlaces);
            }
        });
    }

    @Override public SearchItem getFirstSuggestion() {
        if (searchPlaces != null && searchPlaces.size() > 0)
            return searchPlaces.get(0);
        return null;
    }

    @Override public void loadPopularPlaces(LatLng location) {
        if (popularPlaces != null && popularPlaces.size() > 0 && (location == null || location.equals(this.location))) {
            showPlaces();
            return;
        }
        this.location = location;
        new ModelImplementation().foursquare(location)
                .flatMap(new Func1<MediaList, Observable<Media>>() {
                    @Override public Observable<Media> call(MediaList mediaList) {
                        return Observable.from(mediaList.getList());
                    }
                })
                .map(new Func1<Media, SearchItem>() {
                    @Override public SearchItem call(Media media) {
                        return new SearchItem(media);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SearchItem>() {
                    @Override public void call(SearchItem searchItem) {
                        popularPlaces.add(searchItem);
                    }
                }, onError(), onComplete());
    }

    private Action0 onComplete() {
        return new Action0() {
            @Override public void call() {
                showPlaces();
            }
        };
    }

    private Action1<Throwable> onError() {
        return new Action1<Throwable>() {
            @Override public void call(Throwable throwable) {
                Timber.e(throwable, "Marker subscription error");
            }
        };
    }

    public void addToHistory(SearchItem searchItem) {
        searchItem.resId = Theme.getHistoryResId();
        searchHistory.add(0, searchItem);
        if (searchPlaces.size() > Constants.HISTORY_SIZE + 1) {
            searchHistory.remove(searchHistory.size() - 1);
        }
    }

    private void showPlaces() {
        ArrayList<SearchItem> places = new ArrayList<>();
        for (SearchItem searchItem : searchHistory) {
            places.add(searchItem);
        }
        places.addAll(popularPlaces);
        geoSearchView.showPopularPlaces(places);
    }

    public class SearchItem {

        @DrawableRes public int resId;
        public String photoUrl;
        public Spannable text;
        public double lat;
        public double lng;
        public Media media;

        public SearchItem(Media media) {
            this.text = media.getComments();
            this.media = media;
            this.photoUrl = media.getThumbnail();
            this.lat = media.getLatitude();
            this.lng = media.getLongitude();
        }

        public SearchItem(GeocodeItem item) {
            this.resId = Theme.getPlaceMarkerResId();
            this.lat = item.geometry.location.lat;
            this.lng = item.geometry.location.lng;
            this.text = new SpannableString(item.formatted_address);
        }
    }
}
