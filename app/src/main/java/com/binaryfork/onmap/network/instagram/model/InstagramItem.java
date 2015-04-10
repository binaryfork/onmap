package com.binaryfork.onmap.network.instagram.model;


import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.binaryfork.onmap.BaseApplication;
import com.binaryfork.onmap.R;
import com.binaryfork.onmap.network.Media;
import com.binaryfork.onmap.network.instagram.MediaTypes;

import java.util.List;

public class InstagramItem implements Media {

    public String attribution;
    public List<String> tags;
    public String type;
    public Long created_time;
    public String link;
    public String id;
    public Location location;
    public User user;
    public Caption caption;
    public Comments comments;
    public InstagramMedia.Medias images;
    public InstagramMedia.Medias videos;

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
        SpannableStringBuilder span = new SpannableStringBuilder();
        if (caption != null)
            span.append(spannableComment(caption.from.username, caption.text));
        if (comments.count > 0)
            for (InstagramItem.Comments.Comment comment : comments.data) {
                span.append(spannableComment("\n" + comment.from.username, comment.text));
            }
        return span;
    }

    private Spannable spannableComment(String username, String comment) {
        Spannable wordtoSpan =
                new SpannableString(username + " " + comment);
        wordtoSpan.setSpan(
                new ForegroundColorSpan(BaseApplication.get().getResources().getColor(R.color.accent)),
                0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return wordtoSpan;
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
