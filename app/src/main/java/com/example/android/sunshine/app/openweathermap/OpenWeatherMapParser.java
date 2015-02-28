package com.example.android.sunshine.app.openweathermap;

import android.util.Log;

import com.example.android.sunshine.app.PrefsUtility;
import com.example.android.sunshine.app.WeatherInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.example.android.sunshine.app.*;

/**
 * Parses the json from Open Weather Map into a list of WeatherInfo
 */
public class OpenWeatherMapParser {

        private final String LOG_TAG = OpenWeatherMapParser.class.getSimpleName();

        private static final int SUCCESS_CODE = 200;
        private static final String ERROR_CODE_FIELD = "cod";

        private PrefsUtility mPrefsUtility;
        private ArrayList<WeatherInfo> mWeather;


        public OpenWeatherMapParser (PrefsUtility prefsUtility, String json) {
            this.mPrefsUtility = prefsUtility;

            if(jsonIsValid(json)) {
                parseJson(json);
            }

        }

        public ArrayList<WeatherInfo> getWeather() {
            return mWeather;
        }

        // checks if the json received is valid
        private boolean jsonIsValid(String json){
            if (json == null || json == "" ) {
                return false;
            }
            int requestStatus;
            try {
                requestStatus = new JSONObject(json).getInt(ERROR_CODE_FIELD);
            } catch (JSONException e) {
                return false;
            }
            return requestStatus == SUCCESS_CODE;

        }

        // Delegates the json parsing to finer-grained classes
        private void parseJson(String json) {
            City city = CityParser.parse(json);
            Coordinates coords = CoordinatesParser.parse(json);
            mWeather = WeatherDataParser.parse(mPrefsUtility, json, city, coords);

        }
}
