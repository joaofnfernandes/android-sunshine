package com.example.android.sunshine.app.test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.android.sunshine.app.data.WeatherContract.LocationEntry;
import com.example.android.sunshine.app.data.WeatherContract.WeatherEntry;
import com.example.android.sunshine.app.data.WeatherDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by joaofernandes on 26/8/14.
 */
public class TestProvider extends AndroidTestCase {

    private static final String LOG_TAG = TestProvider.class.getSimpleName();
    private static final String TEST_CITY_NAME = "North Pole";
    private static final String TEST_LOCATION = "99705";
    private static final String TEST_DATE = "20141205";

    public void testGetType() {

        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(type, WeatherEntry.CONTENT_TYPE);

        String testLocation = "94074";
        type = mContext.getContentResolver().getType(WeatherEntry.buildWeatherLocation(testLocation));
        assertEquals(type, WeatherEntry.CONTENT_TYPE);

        // content://com.example.android.sunshine.app/weather/94074/20140901
        String testDate = "20140901";
        // vnd.android.cursor.item/com.example.android.sunshine.app/weather
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
        assertEquals(type, WeatherEntry.CONTENT_ITEM_TYPE);

        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        assertEquals(type, LocationEntry.CONTENT_TYPE);

        long testId = 1L;
        type = mContext.getContentResolver().getType(LocationEntry.getLocationById(testId));
        assertEquals(type, LocationEntry.CONTENT_ITEM_TYPE);
    }

    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    public void testInsertReadProvider() {
        WeatherDbHelper dbHelper = new WeatherDbHelper(this.mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long locationRowId = insertLocationData();
        insertWeatherData(locationRowId);
        dbHelper.close();

        int affectedRows = deleteWeather();
        assertTrue(affectedRows > 0);
    }

    public void testUpdateDeleteLocation() {
        // Delete locations in db to avoid conflicts with unique columns
        Uri locationUri = LocationEntry.CONTENT_URI;
        mContext.getContentResolver().delete(locationUri, null, null);

        ContentValues northPoleLocation = createNorthPoleLocationValues();
        Uri locationIdUri = mContext.getContentResolver().insert(locationUri, northPoleLocation);
        long locationId = ContentUris.parseId(locationIdUri);

        // Location inserted
        assertTrue(locationId != -1);

        ContentValues southPoleLocation = new ContentValues(northPoleLocation);
        southPoleLocation.put(LocationEntry._ID, locationId);
        southPoleLocation.put(LocationEntry.COLUMN_CITY_NAME, "South Pole");

        final String locationSelection = String.format("%1$s.%2$s = ?",
                                            LocationEntry.TABLE_NAME,
                                            LocationEntry._ID);
        String[] locationSelectionArgs = {String.valueOf(locationId)};

        int affectedRows = mContext.getContentResolver()
                .update(locationUri, southPoleLocation, locationSelection, locationSelectionArgs);

        assertEquals(affectedRows, 1);

        Cursor cursor = mContext.getContentResolver()
                .query(locationUri, null, locationSelection, locationSelectionArgs, null);

        validateCursor(cursor, southPoleLocation);
        cursor.close();

        affectedRows = mContext.getContentResolver()
                .delete(LocationEntry.CONTENT_URI, locationSelection, locationSelectionArgs);

        assertEquals(affectedRows, 1);
    }


    private static ContentValues createNorthPoleLocationValues() {
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
        values.put(LocationEntry.COLUMN_COORD_LAT, 64.772);
        values.put(LocationEntry.COLUMN_COORD_LONG, -147.355);

        return values;
    }

    private static ContentValues createWeatherValues(long locationRowId) {

        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, TEST_DATE);
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

    private long insertLocationData() {

        // Create data
        ContentValues testValues = createNorthPoleLocationValues();

        // Insert data
        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // Data inserted, now test if we can read it back with /location
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI, null, null, null, null);
        validateCursor(cursor, testValues);

        // And test again with /locations/{id}
        cursor = mContext.getContentResolver().query(
                LocationEntry.getLocationById(locationRowId), null, null, null, null);
        validateCursor(cursor, testValues);

        cursor.close();
        return locationRowId;
    }

    private void insertWeatherData(long locationRowId) {

        // Create data
        ContentValues testValues = createWeatherValues(locationRowId);

        // Insert data

        Uri insertUri = mContext.getContentResolver()
                                .insert(WeatherEntry.CONTENT_URI, testValues);


        // Data inserted, now test if we can read it back with /weather
        Cursor cursor = mContext.getContentResolver().query(
                            WeatherEntry.CONTENT_URI, null, null, null, null);
        validateCursor(cursor, testValues);
        cursor.close();

        Uri uri = WeatherEntry.buildWeatherLocation(TEST_LOCATION);
        cursor = mContext.getContentResolver().query(uri , null, null, null, null);
        validateCursor(cursor, testValues);
        cursor.close();

        uri = WeatherEntry.buildWeatherLocationWithStartDate(TEST_LOCATION, TEST_DATE);
        cursor = mContext.getContentResolver().query(
                uri, null, null, null, null);
        validateCursor(cursor, testValues);
        cursor.close();

        uri = WeatherEntry.buildWeatherLocationWithDate(TEST_LOCATION, TEST_DATE);
        cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        validateCursor(cursor, testValues);
        cursor.close();
    }

    private int deleteWeather() {
        // Delete all weather data
        Uri weatherUri = WeatherEntry.CONTENT_URI;
        return mContext.getContentResolver().delete(weatherUri, null, null);
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
