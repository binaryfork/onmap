package com.binaryfork.onmap.view.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuView;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.presenter.SearchItem;
import com.binaryfork.onmap.presenter.SearchPresenterImplementation;
import com.binaryfork.onmap.util.Animations;
import com.binaryfork.onmap.view.map.MediaMapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.balysv.materialmenu.MaterialMenuDrawable.IconState.ARROW;
import static com.balysv.materialmenu.MaterialMenuDrawable.IconState.BURGER;

public class SearchFragment extends Fragment implements GeoSearchView {

    @InjectView(R.id.results) ListView listView;
    @InjectView(R.id.pb) ProgressBar progressBar;
    @InjectView(R.id.logo) View logo;
    @InjectView(R.id.search) EditText editText;
    @InjectView(R.id.material_menu_button) MaterialMenuView materialMenu;
    @InjectView(R.id.clear) View clear;

    private SearchAdapter searchAdapter;
    private MediaMapView mediaMapView;
    private SearchPresenterImplementation searchPresenter;
    private boolean searchVisible;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_layout, container);
        ButterKnife.inject(this, view);

        materialMenu.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (materialMenu.getState() == BURGER)
                    mediaMapView.onMenuClick();
                else {
                    hide();
                }
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        openMedia(searchPresenter.getFirstSuggestion());
                        return true;
                    }
                }
                return false;
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        openMedia(searchPresenter.getFirstSuggestion());
                        return true;
                    }
                }
                return false;
            }
        });
        searchAdapter = new SearchAdapter(getActivity());
        listView.setAdapter(searchAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openMedia((SearchItem) searchAdapter.getItem(position));
            }
        });


        if (searchPresenter == null) {
            searchPresenter = new SearchPresenterImplementation(this);
        } else if (searchVisible) {
            openSearch();
        }
        searchPresenter.suggestGeoLocations(editText);

        return view;
    }

    private void openMedia(SearchItem searchItem) {
        searchPresenter.addToHistory(searchItem);
        hide();
        if (searchItem.media == null)
            mediaMapView.goToLocation(new LatLng(searchItem.lat, searchItem.lng));
        else
            mediaMapView.openPhoto(searchItem.media);
    }

    @OnClick(R.id.logo) public void openSearch() {
        logo.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        editText.setVisibility(View.VISIBLE);
        editText.requestFocus();
        clear.setVisibility(View.VISIBLE);
        materialMenu.animateState(ARROW);
        Animations.moveFromTop(listView);
        searchAdapter.clear();
        searchPresenter.loadPopularPlaces(mediaMapView.getLocation());
        searchVisible = true;
    }

    @OnClick(R.id.clear) public void clear() {
        editText.getText().clear();
        searchAdapter.clear();
        searchPresenter.loadPopularPlaces(mediaMapView.getLocation());
    }

    @Override public boolean isShown() {
        return listView.isShown();
    }

    @Override public void hide() {
        Animations.moveToTopHide(listView);
        materialMenu.animateState(BURGER);
        editText.setVisibility(View.GONE);
        logo.setVisibility(View.VISIBLE);
        clear.setVisibility(View.GONE);
        searchVisible = false;
    }

    @Override public void showSuggestions(ArrayList<SearchItem> items) {
        searchAdapter.setData(items);
    }

    @Override public void showPopularPlaces(ArrayList<SearchItem> items) {
        searchAdapter.setData(items);
    }

    @Override public void setMediaMapView(MediaMapView mediaMapView) {
        this.mediaMapView = mediaMapView;
    }

    @Override public void showProgress(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

}
