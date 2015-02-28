package com.example.android.sunshine.app.openweathermap;

import android.net.Uri;
import android.util.Log;
import android.content.Context;

import com.example.android.sunshine.app.PrefsUtility;
import com.example.android.sunshine.app.WeatherInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Open Weather Map API
 */
public class OpenWeatherMap {

    private static final String LOG_TAG = OpenWeatherMap.class.getSimpleName();

    private static final int DAYS = 14;
    private static final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    private static final String QUERY_PARAM = "q";
    private static final String MODE_PARAM = "mode";
    private static final String UNITS_PARAM = "units";
    private static final String DAYS_PARAM = "cnt";

    private static final String FORMAT_VAL = "json";
    private static final String UNITS_VAL = "metric";

    private PrefsUtility mPrefsUtility;
    private String mJson;
    private OpenWeatherMapParser mOpenWeatherParser;


    public OpenWeatherMap(Context context) {
        this.mPrefsUtility = new PrefsUtility(context);
    }

    /* Synchronously fetches data from open weather map API
       By default returns a list of WeatherInfo for the next 14 days
    */
    public ArrayList<WeatherInfo> GetWeather() {
        // try to fetch json
        String json = fetchJson(DAYS);
        // todo: this is ugly, I should always be able to invoke openWeatherMapParser.getWeather
        if(json != "") {
            mJson = json;
            this.mOpenWeatherParser = new OpenWeatherMapParser(mPrefsUtility, json);
        }
        // always return an ArrayList
        return mOpenWeatherParser == null ?
                    new ArrayList<WeatherInfo>() :
                    mOpenWeatherParser.getWeather();
    }


    private String fetchJson(int days){
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String json = null;
        Uri url = null;

        try {
            url = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, mPrefsUtility.getCurrentLocation())
                    .appendQueryParameter(MODE_PARAM, FORMAT_VAL)
                    .appendQueryParameter(UNITS_PARAM, UNITS_VAL)
                    .appendQueryParameter(UNITS_PARAM, UNITS_VAL)
                    .appendQueryParameter(DAYS_PARAM, String.valueOf(days))
                    .build();

            connection = (HttpURLConnection) new URL(url.toString()).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputSteam = connection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputSteam == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputSteam));
            String line;
            while((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            json = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "IO error");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "IO error");
                }
            }
        }
        if(json != null) {
            return json.toLowerCase();
        } else {
            return "";
        }
    }
}
