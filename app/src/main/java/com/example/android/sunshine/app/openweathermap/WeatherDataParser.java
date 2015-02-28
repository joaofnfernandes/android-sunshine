package com.example.android.sunshine.app.openweathermap;

import android.util.Log;

import com.example.android.sunshine.app.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Parses the weather from an Open Weather Map API
 */
public class WeatherDataParser {
    private static final String LOG_TAG             = "WeatherDataParser";
    private static final String WEATHER_ARRAY_COUNT = "cnt";
    private static final String WEATHER_ARRAY       = "list";
    private static final String DATE                = "dt";
    private static final String TEMPERATURE_OBJ     = "temp";
    private static final String MIN_TEMP            = "min";
    private static final String MAX_TEMP            = "max";
    private static final String PRESSURE            = "pressure";
    private static final String HUMIDITY            = "humidity";
    private static final String WIND                = "speed";
    private static final String WEATHER_DESC_OBJ    = "weather";
    private static final String SHORT_DESC          = "main";
    private static final String LONG_DESC           = "description";


    // returns a list of weather info for several days
    public static ArrayList<WeatherInfo> parse(PrefsUtility prefs, String json, City city, Coordinates coords) {
        ArrayList<WeatherInfo> weatherList = new ArrayList<WeatherInfo>();
        WeatherInfo weather;

        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray weatherObj = jsonObj.getJSONArray(WEATHER_ARRAY);
            int days = jsonObj.getInt(WEATHER_ARRAY_COUNT);

            for (int i = 0; i < days; i++) {
                weather = parse(json, i);
                // if weather has default values, don't add it to list
                if(!weather.isDefault()) {
                    weather.setPrefsUtility(prefs);
                    weather.setCountry(city.getCountry());
                    weather.setCity(city.getCity());
                    weather.setLatitude(coords.getLat());
                    weather.setLongitude(coords.getLong());

                    weatherList.add(weather);
                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing weather data ");
        }
        return weatherList;
    }

    // returns the weather info for the specified day
    private static WeatherInfo parse(String json, int index) {
        WeatherInfo weather = new WeatherInfo();
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONObject weatherObj = jsonObj.getJSONArray(WEATHER_ARRAY).getJSONObject(index);

            // Set date
            long date = weatherObj.getLong(DATE);
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(date * 1000);
            weather.setDate(cal);

            // Set temperature
            JSONObject temperature = weatherObj.getJSONObject(TEMPERATURE_OBJ);
            weather.setMinTemperature(temperature.getDouble(MIN_TEMP));
            weather.setMaxTemperature(temperature.getDouble(MAX_TEMP));

            // Set pressure, humidity, and wind
            weather.setPressure(weatherObj.getDouble(PRESSURE));
            weather.setHumidity(weatherObj.getDouble(HUMIDITY));
            weather.setWind(weatherObj.getDouble(WIND));

            // Set description
            JSONObject description = weatherObj.getJSONArray(WEATHER_DESC_OBJ).getJSONObject(0);
            weather.setShortDesc(description.getString(SHORT_DESC));
            weather.setLongDesc(description.getString(LONG_DESC));

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing weather data");
        }

        return weather;
    }


}