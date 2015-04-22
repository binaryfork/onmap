package com.quinny898.library.persistentsearch;

import android.graphics.drawable.Drawable;

public class SearchResult {
    public String title;
    public Drawable icon;
    public double lat;
    public double lng;

    /**
     * Create a search result with text and an icon
     * @param title
     * @param icon
     */
    public SearchResult(String title, Drawable icon) {
        this.title = title;
        this.icon = icon;
    }
    /**
     * Create a search result with text and an icon
     * @param title
     * @param icon
     */
    public SearchResult(String title, Drawable icon, double lat, double lng) {
       this.title = title;
        this.icon = icon;
        this.lat = lat;
        this.lng = lng;
    }
    
    /**
     * Return the title of the result
     */
    @Override
    public String toString() {
        return title;
    }
    
}