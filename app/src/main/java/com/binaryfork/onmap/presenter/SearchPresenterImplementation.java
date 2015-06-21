package com.binaryfork.onmap.presenter;

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
import com.binaryfork.onmap.view.search.SearchView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class SearchPresenterImplementation implements SearchPresenter {

    private SearchView searchView;
    private ArrayList<SearchItem> popularPhotos = new ArrayList<>();
    private ArrayList<SearchItem> popularPlaces = new ArrayList<>();
    private ArrayList<SearchItem> searchPlaces = new ArrayList<>();
    private ArrayList<SearchItem> searchHistory = new ArrayList<>();
    private LatLng location;

    public SearchPresenterImplementation(SearchView searchView) {
        this.searchView = searchView;
//        loadRecentPhotos();
    }

    @Override public void suggestGeoLocations(EditText editText) {
        GeoSearchModel.subscribe(editText, new Action1<GeocodeResults>() {
            @Override public void call(GeocodeResults geocodeResults) {
                searchPlaces = new ArrayList<>();
                for (GeocodeItem item : geocodeResults.results) {
                    searchPlaces.add(new SearchItem(item));
                }
                searchView.showSuggestions(searchPlaces);
            }
        });
    }

    @Override public SearchItem getFirstSuggestion() {
        if (searchPlaces != null && searchPlaces.size() > 0)
            return searchPlaces.get(0);
        return null;
    }

    public void addToHistory(SearchItem searchItem) {
        for (int i = 0; i < searchHistory.size(); i++) {
            if (searchHistory.get(i).text.equals(searchItem.text))
                searchHistory.remove(i);
        }
        searchItem.resId = Theme.getHistoryResId();
        searchHistory.add(0, searchItem);
        if (searchHistory.size() > Constants.HISTORY_SIZE) {
            searchHistory.remove(searchHistory.size() - 1);
        }
    }

    private void showPlaces() {
        ArrayList<SearchItem> places = new ArrayList<>();
/*        if (searchHistory.size() > 0)
            places.add(new SearchItem("Last searched"));
        for (SearchItem searchItem : searchHistory) {
            places.add(searchItem);
        }*/
        if (popularPlaces.size() > 0) {
            places.add(new SearchItem("Popular places nearby"));
            places.addAll(popularPlaces);
        } else if (popularPhotos.size() > 0) {
            places.add(new SearchItem("Interesting photos"));
            for (SearchItem searchItem : popularPhotos) {
                places.add(searchItem);
            }
        }
        searchView.showPopularPlaces(places);
    }

    @Override public void loadPopularPlaces(LatLng location) {
        if (popularPlaces.size() > 0 && location.equals(this.location)) {
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
                        SearchItem searchItem = new SearchItem(media);
                        searchItem.text = new SpannableString(media.getComments());
                        return searchItem;
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
                Timber.e(throwable, "SearchPresenterImplementation");
            }
        };
    }

    @Override public void loadPopularPhotos() {
        popularPhotos = new ArrayList<>();
        new ModelImplementation().flickrPopular()
                .flatMap(new Func1<MediaList, Observable<Media>>() {
                    @Override public Observable<Media> call(MediaList mediaList) {
                        Timber.i("list " + mediaList.getList().size());
                        return Observable.from(mediaList.getList());
                    }
                })
                .map(new Func1<Media, SearchItem>() {
                    @Override public SearchItem call(Media media) {
                        SearchItem searchItem = new SearchItem(media);
                        searchItem.text = new SpannableString(media.getTitle());
                        return searchItem;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SearchItem>() {
                    @Override public void call(SearchItem searchItem) {
                        if (searchItem.lat != 0 && popularPhotos.size() < 6)
                            popularPhotos.add(searchItem);
                    }
                });
    }

    @Override public void loadRecentPhotos() {
        popularPhotos = new ArrayList<>();
        new ModelImplementation().instagram(location)
                .flatMap(new Func1<MediaList, Observable<Media>>() {
                    @Override public Observable<Media> call(MediaList mediaList) {
                        Timber.i("list " + mediaList.getList().size());
                        return Observable.from(mediaList.getList());
                    }
                })
                .map(new Func1<Media, SearchItem>() {
                    @Override public SearchItem call(Media media) {
                        SearchItem searchItem = new SearchItem(media);
                        searchItem.text = new SpannableString(media.getTitle());
                        return searchItem;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SearchItem>() {
                    @Override public void call(SearchItem searchItem) {
                        if (searchItem.lat != 0 && popularPhotos.size() < 6)
                            popularPhotos.add(searchItem);
                    }
                });
    }
}
