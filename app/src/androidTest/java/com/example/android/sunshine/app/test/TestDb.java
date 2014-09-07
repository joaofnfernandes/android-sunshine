package com.example.android.sunshine.app.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;
import java.util.Set;

import com.example.android.sunshine.app.Weather;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherContract.*;
import com.example.android.sunshine.app.data.WeatherDbHelper;

import java.util.Map;

/**
 * Created by joaofernandes on 26/8/14.
 */
public class TestDb extends AndroidTestCase {

    private static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        WeatherDbHelper dbHelper = new WeatherDbHelper(this.mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long locationRowId = insertLocationData(db);
        insertWeatherData(db, locationRowId);

        dbHelper.close();
    }

    private static ContentValues createNorthPoleLocationValues() {
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_CITY_NAME, "North Pole");
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, "99705");
        values.put(LocationEntry.COLUMN_COORD_LAT, 64.772);
        values.put(LocationEntry.COLUMN_COORD_LONG, -147.355);

        return values;
    }

    private static ContentValues createWeatherValues(long locationRowId) {

        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 23);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 16);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

        return weatherValues;
    }

    private long insertLocationData(SQLiteDatabase db) {

        // Create data
        ContentValues testValues = createNorthPoleLocationValues();

        // Insert data
        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, testValues);
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data inserted, now test if we can read it back
        Cursor cursor = db.query(LocationEntry.TABLE_NAME, null, null, null, null, null, null);
        validateCursor(cursor, testValues);

        cursor.close();
        return locationRowId;
    }

    private void insertWeatherData(SQLiteDatabase db, long locationRowId) {

        // Create data
        ContentValues testValues = createWeatherValues(locationRowId);

        // Insert data
        long weatherRowId;
        weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, testValues);
        assertTrue(weatherRowId != -1);
        Log.d(LOG_TAG, "New weather row id: " + weatherRowId);

        // Data inserted, now test if we can read it back
        Cursor cursor = db.query(WeatherEntry.TABLE_NAME, null, null, null, null, null, null);
        validateCursor(cursor, testValues);
        cursor.close();


    }

    private void validateCursor(Cursor cursor, ContentValues expectedValues) {
        assertTrue(cursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for(Map.Entry<String,Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = cursor.getColumnIndex(columnName);
            assertTrue(index != -1);
            String expectedValue = entry.getValue().toString();
            String actualValue = cursor.getString(index);
            assertEquals(expectedValue, actualValue);
        }
        cursor.close();

    }
}
