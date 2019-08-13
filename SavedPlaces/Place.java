package com.etoitau.savedplaces;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class Place {
    private LatLng latLng;
    private String name;

    public Place (String name, LatLng latLng) {
        this.name = name;
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public double getLat() {
        return latLng.latitude;
    }

    public double getLng() {
        return latLng.longitude;
    }

    public String getName() {
        return name;
    }
}
