package com.binaryfork.onmap.model.foursquare.model;

import com.binaryfork.onmap.model.Media;
import com.binaryfork.onmap.model.MediaList;

import java.util.ArrayList;

public class FoursquareResponse implements MediaList {

    public Response response;

    @Override
    public ArrayList<? extends Media> getList() {
        return response.groups.get(0).items;
    }

    public class Response {

        public ArrayList<Groups> groups;

        public class Groups {
            public ArrayList<FoursquareItem> items;

        }

    }
}