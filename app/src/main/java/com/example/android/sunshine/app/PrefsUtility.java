package com.example.android.sunshine.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by joaofernandes on 7/9/14.
 */
public class PrefsUtility {

    private Context context;

    public PrefsUtility(Context context) {
        this.context = context;
    }

    public Units getCurrentUnits() {
        final String unitsKey = context.getString(R.string.prefs_unit_key);
        final String unitsDefaultValue = context.getString(R.string.prefs_unit_default);

        final String mMetricUnits = context.getString(R.string.prefs_unit_metric);
        final String mImperialUnits = context.getString(R.string.prefs_unit_imperial);

        String units = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(unitsKey, unitsDefaultValue);

        if (units.equals(mMetricUnits)) {
            return Units.METRIC;
        } else if (units.equals(mImperialUnits)) {
            return Units.IMPERIAL;
        } else {
            throw new AssertionError();
        }
    }

    public String getCurrentLocation (){
        final String locationKey = context.getString(R.string.pref_location_key);
        final String locationDefaultValue = context.getString(R.string.pref_location_default);

        return PreferenceManager.getDefaultSharedPreferences(context)
                                .getString(locationKey, locationDefaultValue);
    }

    // If the user changes the preferences
    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChanged =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    };
}
