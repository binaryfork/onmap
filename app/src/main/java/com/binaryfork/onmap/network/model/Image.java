package com.binaryfork.onmap.network.model;

public class Image {
    public String url;
    public Integer width;
    public Integer height;

    public class Images {
        public Image low_resolution;
        public Image thumbnail;
        public Image standard_resolution;
    }
}
