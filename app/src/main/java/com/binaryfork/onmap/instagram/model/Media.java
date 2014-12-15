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

    public class Location {
        public String id;
        public String name;
        public float latitude;
        public float longitude;
    }

    public class MediaResponse {
        public List<Media> data;
    }
}
