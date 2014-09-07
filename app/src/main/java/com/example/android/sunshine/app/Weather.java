package com.example.android.sunshine.app;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *   Uses the open weather api to fetch weather
 *
 */
// todo: implement retry mechanism
public class Weather {
    private static final String LOG_TAG = Weather.class.getSimpleName();
    private static final int DAYS = 14;

    private final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    private final String QUERY_PARAM = "q";
    private final String MODE_PARAM = "mode";
    private final String FORMAT = "json";
    private final String UNITS_PARAM = "units";
    private final String UNITS_VAL = "metric";
    private final String DAYS_PARAM = "cnt";

    private String postalCode;
    private Units units;


    public Weather(Units units, String postalCode) {
        this.units = units;
        this.postalCode = postalCode;
    }

    public List<WeatherInfo> GetWeather() {
       String json = getJson(DAYS);
       List<WeatherInfo> weather = new ArrayList<WeatherInfo>();
       WeatherInfo dailyWeather;

       for (int i = 0; i < DAYS; i++) {
           dailyWeather = jsonToWeatherInfo(json, i);
           if (dailyWeather != null) {
               weather.add(dailyWeather);
           }
       }
       return weather;
    }

    private String getJson(int days){
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String json = null;
        Uri url = null;

        try {
            url = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, postalCode)
                    .appendQueryParameter(MODE_PARAM, FORMAT)
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
            //todo: try to find the cause of the error and display to user
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log error?
                }
            }
        }
        return json != null ? json.toLowerCase() : "";

    }
    private boolean requestSuccessful(String json) {
        if (json == null || json == "" ) {
            return false;
        }
        int requestStatus;
        try {
            requestStatus = new JSONObject(json).getInt("cod");
        } catch (JSONException e) {
            return false;
        }
        return requestStatus == 200 ? true : false;

    }
    private WeatherInfo jsonToWeatherInfo(String json, int day){
        if(!requestSuccessful(json)){
            return null;
        }

        JSONObject weather, dailyWeather, dailyTemp, dailyDesc = null;
        WeatherInfo weatherInfo = null;
        long date;
        double min, max;
        String description;

        try {
            weather = new JSONObject(json);
            dailyWeather = weather.getJSONArray("list").getJSONObject(day);
            dailyTemp = dailyWeather.getJSONObject("temp");
            dailyDesc = dailyWeather.getJSONArray("weather").getJSONObject(0);

            date = dailyWeather.getLong("dt");
            min = dailyTemp.getDouble("min");
            max = dailyTemp.getDouble("max");
            description = dailyDesc.getString("main");

        }catch (JSONException e) {
            return null;
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(date * 1000);

        return new WeatherInfo(calendar, min, max, description, units);
    }

}