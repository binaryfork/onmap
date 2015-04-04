package com.binaryfork.onmap.network.model;

public class GeocodeItem {
    public String formatted_address;
    public Geometry geometry;

    public class Geometry {
        public GeoLocation location;

        public class GeoLocation {
            public double lat;
            public double lng;
        }
    }
}
