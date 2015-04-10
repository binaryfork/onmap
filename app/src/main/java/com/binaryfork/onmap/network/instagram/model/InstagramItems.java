package com.binaryfork.onmap.network.instagram.model;

import com.binaryfork.onmap.network.MediaList;

import java.util.ArrayList;

public class InstagramItems implements MediaList {
    public ArrayList<InstagramItem> data;

    @Override
    public ArrayList getList() {
        return data;
    }
}
