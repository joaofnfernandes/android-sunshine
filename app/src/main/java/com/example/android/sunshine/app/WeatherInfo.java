package com.example.android.sunshine.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Represents information about the weather on a specific coordinate
 *
 */
public class WeatherInfo {
    private static final String LOG_TAG = WeatherInfo.class.getSimpleName();

    private PrefsUtility mPrefsUtility;

    private Calendar mDate = new GregorianCalendar();
    private String mCity, mCountry;
    private double mMinTemperature, mMaxTemperature, mPressure, mHumidity, mWind, mLatitude, mLongitude;
    private String mShortDesc, mLongDesc;

    public WeatherInfo() {}

    public WeatherInfo(PrefsUtility prefsUtility,
                       double latitude,
                       double longitude,
                       Calendar date,
                       String country,
                       String city,
                       double min_tmp,
                       double max_tmp,
                       double pressure,
                       double humidity,
                       double wind,
                       String short_desc,
                       String long_desc) {
        this.mPrefsUtility = prefsUtility;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mDate = date;
        this.mCountry = country;
        this.mCity = city;
        this.mMinTemperature = min_tmp;
        this.mMaxTemperature = max_tmp;
        this.mPressure = pressure;
        this.mHumidity = humidity;
        this.mWind = wind;
        this.mShortDesc = short_desc;
        this.mLongDesc = long_desc;

    }

    public Calendar getDate() {
        return mDate;
    }

    public String getCity() {
        return mCity;
    }

    public String getCountry() {
        return mCountry;
    }

    public double getMinTemperatureInCelsius() {
        return mMinTemperature;
    }

    public double getMaxTemperatureInCelsius() {
        return mMaxTemperature;
    }

    public double getPressure() {
        return mPressure;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public double getWind() {
        return mWind;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String getShortDesc() {
        return mShortDesc;
    }

    public String getLongDesc() {
        return mLongDesc;
    }

    public void setDate(Calendar mDate) {
        this.mDate = mDate;
    }

    public void setCity(String mCity) {
        this.mCity = mCity;
    }

    public void setCountry(String mCountry) {
        this.mCountry = mCountry;
    }

    public void setMinTemperature(double mMinTemperature) {
        this.mMinTemperature = mMinTemperature;
    }

    public void setMaxTemperature(double mMaxTemperature) {
        this.mMaxTemperature = mMaxTemperature;
    }

    public void setPressure(double mPressure) {
        this.mPressure = mPressure;
    }

    public void setHumidity(double mHumidity) {
        this.mHumidity = mHumidity;
    }

    public void setWind(double mWind) {
        this.mWind = mWind;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public void setShortDesc(String mShortDesc) {
        this.mShortDesc = mShortDesc;
    }

    public void setLongDesc(String mLongDesc) {
        this.mLongDesc = mLongDesc;
    }

    public void setPrefsUtility(PrefsUtility mPrefsUtility) {
        this.mPrefsUtility = mPrefsUtility;
    }

    public boolean isDefault() {
        WeatherInfo other = new WeatherInfo();
        boolean isEqual =
                            this.mDate.compareTo(other.mDate)        == 0 &&
                            this.mCity.compareTo(other.mCity)        == 0 &&
                            this.mCountry.compareTo(other.mCountry)  == 0 &&
                            this.mMinTemperature == other.mMinTemperature &&
                            this.mMaxTemperature == other.mMaxTemperature &&
                            this.mPressure == other.mHumidity             &&
                            this.mHumidity == other.mHumidity             &&
                            this.mWind == other.mWind                     &&
                            this.mLatitude == other.mLongitude            &&
                            this.mLongitude == other.mLongitude           &&
                            mShortDesc.compareTo(other.mShortDesc)   == 0 &&
                            mLongDesc.compareTo(other.mLongDesc)     == 0;



        return isEqual;
    }

    /*
                June 21
                Rainy
                15˚-21˚
             */

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(new SimpleDateFormat("MMM d").format(mDate.getTime()));
        builder.append(String.format("\n%s\n", mShortDesc));

        switch (mPrefsUtility.getCurrentUnits()) {
            case METRIC:
                builder.append(String.format("%f˚-%f˚",
                                TemperatureConverter.toCelsius(mMinTemperature),
                                TemperatureConverter.toCelsius(mMaxTemperature)));
                break;
            case IMPERIAL:
                builder.append(String.format("%f˚-%f˚",
                        TemperatureConverter.toFahrenheit(mMinTemperature),
                        TemperatureConverter.toFahrenheit(mMaxTemperature)));
                break;
            default:
                throw new IllegalStateException(LOG_TAG);
        }


        return builder.toString();
    }
}