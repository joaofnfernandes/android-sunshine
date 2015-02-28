package com.example.android.sunshine.app;

/**
 * Represents coordinates on a globe
 */
public class Coordinates {
    private double mLat;
    private double mLong;

    public Coordinates(double lat, double lon) {
        this.mLat = lat;
        this.mLong = lon;
    }

    public double getLat() {
        return mLat;
    }

    public double getLong() {
        return mLong;
    }
}
