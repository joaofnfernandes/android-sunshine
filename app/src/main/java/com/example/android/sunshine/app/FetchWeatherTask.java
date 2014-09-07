package com.example.android.sunshine.app;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
        PrefsUtility prefs = new PrefsUtility(mContext);
        Weather weather = new Weather(prefs.getCurrentUnits(), prefs.getCurrentLocation());

        return weather.GetWeather();
    }

    protected void onPostExecute(List<WeatherInfo> weather) {
        if (! weather.isEmpty()) {
            mForecastAdapter.clear();
            mForecastAdapter.addAll(weather);
            mForecastAdapter.notifyDataSetChanged();
        }
    }
}
