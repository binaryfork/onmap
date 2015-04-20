package com.binaryfork.onmap.network.instagram.model;


import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;

import com.binaryfork.onmap.BaseApplication;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.network.Media;
import com.binaryfork.onmap.network.instagram.MediaTypes;
import com.binaryfork.onmap.util.Spanny;

import java.util.List;

public class InstagramItem implements Media {

    private String attribution;
    private List<String> tags;
    private String type;
    private Long created_time;
    private String link;
    private String id;
    private Location location;
    private User user;
    private Caption caption;
    private Comments comments;
    private InstagramMedia.Medias images;
    private InstagramMedia.Medias videos;

    transient private Bitmap thumbBitmap;

    @Override
    public String getPhotoUrl() {
        return images.standard_resolution.url;
    }

    @Override
    public String getThumbnail() {
        return images.thumbnail.url;
    }

    @Override
    public String getVideoUrl() {
        return videos.standard_resolution.url;
    }

    @Override
    public String getUsername() {
        return user.username;
    }

    @Override
    public String getUserpic() {
        return user.profile_picture;
    }

    @Override
    public String getSiteUrl() {
        return link;
    }

    @Override
    public float getLatitude() {
        return location.latitude;
    }

    @Override
    public float getLongitude() {
        return location.longitude;
    }

    @Override
    public boolean isVideo() {
        return type.equals(MediaTypes.VIDEO);
    }

    @Override
    public long getCreatedDate() {
        return created_time;
    }

    @Override
    public Spannable getComments() {
        int authorColor = BaseApplication.get().getResources().getColor(R.color.accent);
        Spanny spanny = new Spanny();
        if (caption != null)
            spanny.append(caption.from.username, new ForegroundColorSpan(authorColor))
                    .append(" " + caption.text);
        if (comments.count > 0)
            for (InstagramItem.Comments.Comment comment : comments.data) {
                spanny.append("\n" + comment.from.username, new ForegroundColorSpan(authorColor))
                        .append(" " + comment.text);
            }
        return spanny.getSpannable();
    }

    @Override public void setThumbBitmap(Bitmap bitmap) {
        thumbBitmap = bitmap;
    }

    @Override public Bitmap getThumbBitmap() {
        return thumbBitmap;
    }

    public class Location {
        public String id;
        public String name;
        public float latitude;
        public float longitude;
    }

    public class User {
        public String username;
        public String profile_picture;
    }

    public class Caption {
        public String text;
        public User from;
    }

    public class Comments {
        public int count;
        public List<Comment> data;

        public class Comment {
            public Long created_time;
            public String text;
            public User from;
        }
    }
}
