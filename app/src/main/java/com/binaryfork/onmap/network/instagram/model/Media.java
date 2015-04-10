package com.binaryfork.onmap.network.instagram.model;

public class Media {
    public String url;
    public Integer width;
    public Integer height;

    public class Medias {
        public Media low_resolution;
        public Media thumbnail;
        public Media standard_resolution;
    }
}
