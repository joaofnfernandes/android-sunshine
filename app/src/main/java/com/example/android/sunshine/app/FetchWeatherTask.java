package com.example.android.sunshine.app;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.example.android.sunshine.app.openweathermap.OpenWeatherMap;

import java.util.List;

/**
 * Created by joaofernandes on 7/9/14.
 */
public class FetchWeatherTask extends AsyncTask<Void, Void, List<WeatherInfo>>  {

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
    private Context mContext;
    private ArrayAdapter<WeatherInfo> mForecastAdapter;


    public FetchWeatherTask(Context context, ArrayAdapter<WeatherInfo> forecastAdapter) {
        this.mContext = context;
        this.mForecastAdapter = forecastAdapter;
    }

    protected List<WeatherInfo> doInBackground(Void... voids) {
        OpenWeatherMap weather = new OpenWeatherMap(mContext);
        // protect if openWeatherMap cannot return anything
        return weather.GetWeather();
    }

    protected void onPostExecute(List<WeatherInfo> weather) {
        if (!weather.isEmpty()) {
            mForecastAdapter.clear();
            mForecastAdapter.addAll(weather);
            mForecastAdapter.notifyDataSetChanged();
        }
    }
}
