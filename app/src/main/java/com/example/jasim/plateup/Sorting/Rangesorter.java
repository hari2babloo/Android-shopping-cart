package com.example.jasim.plateup.Sorting;

import android.location.Location;

import com.example.jasim.plateup.Content;

import java.util.ArrayList;

/**
 * Created by jasim on 30.12.2017.
 */

public class Rangesorter extends ArrayList<Content> {

    private Location userLocation;

    public void setUserLocation(Location location) {
        userLocation = location;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    @Override
    public boolean add(Content content) {



        return false;
    }



}
