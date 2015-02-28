package com.example.android.sunshine.app;

/**
 * Represents a City
 */
public class City {
    private String mCountry;
    private String mCity;

    public City(String country, String city) {
        this.mCountry = country;
        this.mCity = city;
    }

    public String getCountry() {
        return mCountry;
    }

    public String getCity() {
        return mCity;
    }
}
