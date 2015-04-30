package com.binaryfork.onmap.model.instagram.model;

public class InstagramMedia {
    public String url;
    public Integer width;
    public Integer height;

    public class Medias {
        public InstagramMedia low_resolution;
        public InstagramMedia thumbnail;
        public InstagramMedia standard_resolution;
    }
}
