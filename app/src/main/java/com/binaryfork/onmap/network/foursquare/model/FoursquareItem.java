package com.binaryfork.onmap.network.foursquare.model;

import android.text.Spannable;
import android.text.style.ForegroundColorSpan;

import com.binaryfork.onmap.BaseApplication;
import com.binaryfork.onmap.R;
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
        return venue.featuredPhotos.items.get(0).getPic();
    }

    @Override
    public String getThumbnail() {
        return venue.featuredPhotos.items.get(0).getThumb();
    }

    @Override
    public String getVideoUrl() {
        return null;
    }

    @Override
    public String getUsername() {
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
        int authorColor = BaseApplication.get().getResources().getColor(R.color.accent);
        Spanny spanny = new Spanny();
        if (tips != null && tips.size() > 0) {
            for (Tip tip : tips) {
                spanny.append(tip.user.firstName + " " + tip.user.lastName, new ForegroundColorSpan(authorColor))
                        .append(" " + tip.text + "\n");
            }
        }
        return spanny.getSpannable();
    }

}
