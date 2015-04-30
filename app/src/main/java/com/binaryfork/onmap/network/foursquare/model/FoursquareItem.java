package com.binaryfork.onmap.network.foursquare.model;

import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import com.binaryfork.onmap.network.Media;
import com.binaryfork.onmap.util.Spanny;

import java.util.ArrayList;

public class FoursquareItem implements Media {

    public Venue venue;
    public ArrayList<Tip> tips;

    public class Venue {

        public String name;
        public Location location;
        public Photos featuredPhotos;

        public class Location {
            public double lat;
            public double lng;
            public String address;
        }

        public class Photos {
            public ArrayList<PhotoItem> items;
        }

        public class PhotoItem {
            public long createdAt;
            public String prefix;
            public String suffix;

            public String getThumb() {
                return prefix + "150x150" + suffix;
            }

            public String getPic() {
                return prefix + "cap600" + suffix;
            }
        }

    }

    public class Tip {
        public String text;
        public User user;

        public class User {
            public String firstName;
            public String lastName;
        }
    }

    @Override
    public String getPhotoUrl() {
        if (venue.featuredPhotos == null || venue.featuredPhotos.items == null || venue.featuredPhotos.items.size() == 0)
            return null;
        return venue.featuredPhotos.items.get(0).getPic();
    }

    @Override
    public String getThumbnail() {
        if (venue.featuredPhotos == null || venue.featuredPhotos.items == null || venue.featuredPhotos.items.size() == 0)
            return null;
        return venue.featuredPhotos.items.get(0).getThumb();
    }

    @Override
    public String getVideoUrl() {
        return null;
    }

    @Override
    public String getTitle() {
        return venue.name;
    }

    @Override
    public String getUserpic() {
        return null;
    }

    @Override
    public String getSiteUrl() {
        return null;
    }

    @Override
    public double getLatitude() {
        return venue.location.lat;
    }

    @Override
    public double getLongitude() {
        return venue.location.lng;
    }

    @Override
    public boolean isVideo() {
        return false;
    }

    @Override
    public long getCreatedDate() {
        return venue.featuredPhotos.items.get(0).createdAt;
    }

    @Override
    public Spannable getComments() {
        Spanny spanny = new Spanny(getTitle());
        if (tips != null && tips.size() > 0) {
            spanny.append("\n");
            for (Tip tip : tips) {
                spanny.append(tip.text, new ForegroundColorSpan(Color.GRAY), new RelativeSizeSpan(0.8f));
            }
        }
        return spanny.getSpannable();
    }

    @Override public String getAdderss() {
        return venue.location.address;
    }

}
