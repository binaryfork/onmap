package com.binaryfork.onmap.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.balysv.materialmenu.MaterialMenuView;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.mvp.GeoSearchModel;
import com.binaryfork.onmap.mvp.GeoSearchView;
import com.binaryfork.onmap.mvp.MapMediaView;
import com.binaryfork.onmap.mvp.ModelImplementation;
import com.binaryfork.onmap.network.Media;
import com.binaryfork.onmap.network.foursquare.model.FoursquareResponse;
import com.binaryfork.onmap.network.google.model.GeocodeItem;
import com.binaryfork.onmap.network.google.model.GeocodeResults;
import com.binaryfork.onmap.util.Animations;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

import static com.balysv.materialmenu.MaterialMenuDrawable.IconState.ARROW;
import static com.balysv.materialmenu.MaterialMenuDrawable.IconState.BURGER;

public class SearchFragment extends Fragment implements GeoSearchView {

    @InjectView(R.id.results) ListView listView;
    @InjectView(R.id.pb) ProgressBar progressBar;
    @InjectView(R.id.logo) View logo;
    @InjectView(R.id.search) EditText editText;
    @InjectView(R.id.material_menu_button) MaterialMenuView materialMenu;

    private ArrayList<Media> items;
    private SearchAdapter searchAdapter;
    private LatLng location;
    private MapMediaView mapMediaView;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_layout, container);
        ButterKnife.inject(this, view);
        materialMenu.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (materialMenu.getState() == BURGER)
                    mapMediaView.onMenuClick();
                else {
                    hide();
                }
            }
        });
        GeoSearchModel.subscribe(editText, new Action1<GeocodeResults>() {
            @Override public void call(GeocodeResults geocodeResults) {
                items.clear();
                for (GeocodeItem item : geocodeResults.results) {
                    SearchItem searchItem = new SearchItem(
                            item.formatted_address,
                            getResources().getDrawable(android.R.drawable.ic_menu_recent_history),
                            item.geometry.location.lat,
                            item.geometry.location.lng);
                    items.add(searchItem);
                }
                searchAdapter.notifyDataSetChanged();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Media media = items.get(position);
                if (media instanceof SearchItem)
                    mapMediaView.goToLocation(new LatLng(media.getLatitude(), media.getLongitude()));
            }
        });
        return view;
    }

    @OnClick(R.id.logo) public void openSearch() {
        logo.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        editText.setVisibility(View.VISIBLE);
        editText.requestFocus();
        materialMenu.animateState(ARROW);
        Animations.moveFromTop(listView);
        LatLng location = mapMediaView.getLocation();
        if (items != null && items.size() > 0 && location.equals(this.location))
            return;
        this.location = location;
        new ModelImplementation().foursquare(location)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<FoursquareResponse>() {
                    @Override public void call(FoursquareResponse foursquareResponse) {
                        items = (ArrayList<Media>) foursquareResponse.getList();
                        searchAdapter = new SearchAdapter(items, getActivity());
                        listView.setAdapter(searchAdapter);
                    }
                });
    }

    @Override public boolean isShown() {
        return listView.isShown();
    }

    @Override public void hide() {
        Animations.moveToTopHide(listView);
        materialMenu.animateState(BURGER);
        editText.setVisibility(View.GONE);
        logo.setVisibility(View.VISIBLE);
    }

    private Action1<Throwable> onError() {
        return new Action1<Throwable>() {
            @Override public void call(Throwable throwable) {
                Timber.e(throwable, "Marker subscription error");
            }
        };
    }

    @Override public void setMapMediaView(MapMediaView mapMediaView) {
        this.mapMediaView = mapMediaView;
    }

    @Override public void showProgress(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
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
