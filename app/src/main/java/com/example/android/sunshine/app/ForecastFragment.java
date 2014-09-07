package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by joaofernandes on 27/7/14.
 */
public class ForecastFragment extends Fragment {

    ArrayAdapter<WeatherInfo> mForecastAdapter;
    NetworkReceiver networkReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mForecastAdapter = new ArrayAdapter<WeatherInfo>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview);

        Command command = new RefreshViewCommand();
        networkReceiver = new NetworkReceiver(command);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        refreshData();

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String forecast = adapterView.getItemAtPosition(i).toString();
                        //Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), DetailActivity.class)
                                .putExtra(Intent.EXTRA_TEXT, forecast);
                        startActivity(intent);

                    }
                }
        );

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    private class RefreshViewCommand implements Command {
        @Override
        public void Execute() {
            refreshData();
        }
    }

    @Override
    public void onResume() {

        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        getActivity().registerReceiver(networkReceiver, intentFilter);
        refreshData();
        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(networkReceiver);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            new FetchWeatherTask(getActivity(), mForecastAdapter).execute();
            return true;
        } else if(item.getItemId() == R.id.action_see_on_map) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            // todo: fix this
            Uri uri = Uri.parse("geo:0,0").buildUpon()
                    .appendQueryParameter("q", new PrefsUtility(getActivity()).getCurrentLocation())
                    .build();
            i.setData(uri);
            //check that there is a maps app
            if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(i);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshData() {
        new FetchWeatherTask(getActivity(), mForecastAdapter).execute();
    }

}
