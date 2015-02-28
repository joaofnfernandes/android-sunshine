package com.example.android.sunshine.app;

import android.content.SharedPreferences;

/**
 * Receives notifications about user preferences changes
 */
public interface PrefsChangeListener {
    public void onChange (SharedPreferences sharedPreferences, String key);
}
