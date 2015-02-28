package com.example.android.sunshine.app.openweathermap;

import android.util.Log;
import com.example.android.sunshine.app.City;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility clas to parse a json from Open Weather Map and return the City info
 */
public class CityParser {

        private static final String LOG_TAG = "CityParser";

        private static final String CITY_OBJ = "city";
        private static final String CITY_NAME_FIELD = "name";
        private static final String COUNTRY_NAME_FIELD = "country";


        public static City parse (String json) {
            JSONObject jsonObject = null,
                    cityObject = null;
            String country = "",
                   city = "";

            try {
                jsonObject = new JSONObject(json);
                cityObject = jsonObject.getJSONObject(CITY_OBJ);

                country = cityObject.getString(COUNTRY_NAME_FIELD);
                city = cityObject.getString(CITY_NAME_FIELD);

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error parsing City");
            }
            return new City(country, city);
        }


}
