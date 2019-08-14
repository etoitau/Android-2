package com.etoitau.savedplaces;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Place implements Serializable {
    private double lat, lng;
    private String name;

    public Place (String name, LatLng latLng) {
        this.name = name;
        this.lat = latLng.latitude;
        this.lng = latLng.longitude;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getName() {
        return name;
    }
}
