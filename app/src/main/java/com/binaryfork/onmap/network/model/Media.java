package com.binaryfork.onmap.network.model;


import java.util.List;

public class Media {

    public String attribution;
    public List<String> tags;
    public String type;
    public Long created_time;
    public String link;
    public Image.Images images;
    public String id;
    public Location location;
    public User user;
    public Caption caption;
    public Comments comments;

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
