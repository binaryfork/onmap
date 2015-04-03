package com.binaryfork.onmap.instagram.model;


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

    public class Comments {
        public List<Comment> data;

        public class Comment {
            public Long created_time;
            public String text;
            public User from;
        }
    }
}
