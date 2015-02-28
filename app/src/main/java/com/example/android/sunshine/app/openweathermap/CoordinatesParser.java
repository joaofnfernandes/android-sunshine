package com.example.android.sunshine.app.openweathermap;

import android.util.Log;

import com.example.android.sunshine.app.Coordinates;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class to parse a json from Open Weather Map and return the coordinates
 */
public class CoordinatesParser {
        private static final String LOG_TAG = "CoordinatesParser";

        private static final String CITY_OBJ = "city";
        private static final String COORDS_OBJ = "coord";
        private static final String LONGITUDE = "lon";
        private static final String LATITUDE = "lat";

        public static Coordinates parse(String json) {
            JSONObject jsonObj = null,
                        coordsObj = null;
            double latitude = 0.0,
                    longitude = 0.0;

            try {
                jsonObj = new JSONObject(json);
                coordsObj = jsonObj.getJSONObject(CITY_OBJ).getJSONObject(COORDS_OBJ);

                latitude = coordsObj.getDouble(LATITUDE);
                longitude = coordsObj.getDouble(LONGITUDE);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error parsing Coordinates");
            }
            return new Coordinates(latitude, longitude);
        }


}
