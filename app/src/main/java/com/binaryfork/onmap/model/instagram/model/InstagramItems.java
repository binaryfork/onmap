package com.binaryfork.onmap.model.instagram.model;

import com.binaryfork.onmap.model.Media;
import com.binaryfork.onmap.model.MediaList;

import java.util.ArrayList;

public class InstagramItems implements MediaList {
    public ArrayList<InstagramItem> data;

    @Override
    public ArrayList<? extends Media> getList() {
        return data;
    }
}
