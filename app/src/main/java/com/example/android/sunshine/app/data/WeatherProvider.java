package com.example.android.sunshine.app.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.location.Location;
import android.net.Uri;

/**
 * Created by joaofernandes on 31/8/14.
 */
public class WeatherProvider extends ContentProvider {

    private static final int WEATHER = 100;
    private static final int WEATHER_WITH_LOCATION = 101;
    private static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    private static final int LOCATION = 300;
    private static final int LOCATION_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private WeatherDbHelper dbHelper;
    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static {
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder.setTables(
                String.format("%1$s INNER JOIN %2$s ON %1$s.%3$s = %2$s.%4$s",
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        WeatherContract.LocationEntry.TABLE_NAME,
                        WeatherContract.WeatherEntry.COLUMN_LOC_KEY,
                        WeatherContract.LocationEntry._ID));
    }

    private static final String sLocationSettingSelection =
            String.format("%1$s.%2$s = ?",
                    WeatherContract.LocationEntry.TABLE_NAME,
                    WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

    private static final String sLocationSettingWithStartDateSelection =
            String.format("%1$s.%2$s = ? and %3$s.%4$s >= ?",
                    WeatherContract.LocationEntry.TABLE_NAME,
                    WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                    WeatherContract.WeatherEntry.TABLE_NAME,
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT);

    private static final String sLocationSettingWithDateSelection =
            String.format("%1$s.%2$s = ? and %3$s.%4$s = ?",
                    WeatherContract.LocationEntry.TABLE_NAME,
                    WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                    WeatherContract.WeatherEntry.TABLE_NAME,
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT);

    private static final String sWeatherIdSelection =
            String.format("%1$s.%2$s = ?",
                    WeatherContract.WeatherEntry.TABLE_NAME,
                    WeatherContract.WeatherEntry._ID);

    private static final String sLocationIdSelection =
            String.format("%1$s.%2$s = ?",
                    WeatherContract.LocationEntry.TABLE_NAME,
                    WeatherContract.LocationEntry._ID);

    @Override
    public boolean onCreate() {
        dbHelper = new WeatherDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            // weather
            case WEATHER:
                cursor = dbHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // weather/{location}?date={d}
            case WEATHER_WITH_LOCATION:
                cursor = getWeatherByLocationSetting(uri, projection, sortOrder);
                break;
            // weather/{date}
            case WEATHER_WITH_LOCATION_AND_DATE:
                cursor = getWeatherByLocationAndDate(uri, projection, sortOrder);
                break;
            // location
            case LOCATION:
                cursor = dbHelper.getReadableDatabase().query(
                            WeatherContract.LocationEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder
                );
                break;
            // location/#
            case LOCATION_ID:
                cursor = dbHelper.getReadableDatabase().query(
                            WeatherContract.LocationEntry.TABLE_NAME,
                            projection,
                            String.format("%s = %d", WeatherContract.WeatherEntry._ID, ContentUris.parseId(uri)),
                            selectionArgs,
                            null,
                            null,
                            sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER_WITH_LOCATION_AND_DATE:
                return  WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case LOCATION:
                return WeatherContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return WeatherContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri resultUri;

        switch (match) {
            // weather
            case WEATHER: {
                long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    resultUri = WeatherContract.WeatherEntry.buildWeatherUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            // location
            case LOCATION: {
                long _id = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    resultUri = WeatherContract.LocationEntry.getLocationById(_id);
                } else {
                    throw new SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int affectedRows = 0;

        switch (match) {
            //delete all weather data
            case WEATHER: {
                affectedRows = dbHelper.getWritableDatabase().delete(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            case LOCATION: {
                affectedRows = dbHelper.getWritableDatabase().delete(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (affectedRows != 0 || selection == null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int affectedRows = 0;

        switch (match) {
            case WEATHER: {
                affectedRows = dbHelper.getWritableDatabase().update(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;
            }
            case LOCATION: {
                affectedRows = dbHelper.getWritableDatabase().update(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (affectedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsInserted = 0;
        long rowId = 0;

        switch (match) {
            case WEATHER: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        rowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if (rowId != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            default:
                super.bulkInsert(uri, values);
        }
        if (rowsInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsInserted;
    }

    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == null) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[] {locationSetting};
        } else {
            selection = sLocationSettingWithStartDateSelection;
            selectionArgs = new String[] {locationSetting, startDate};
        }

        return sWeatherByLocationSettingQueryBuilder.query(
                dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

    }

    private Cursor getWeatherByLocationAndDate(Uri uri, String[] projection, String sortOrder) {
        String location = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String date = WeatherContract.WeatherEntry.getDateFromUri(uri);
        String[] selectionArgs = {location, date};

        return sWeatherByLocationSettingQueryBuilder.query(
                dbHelper.getReadableDatabase(),
                projection,
                sLocationSettingWithDateSelection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/*", WEATHER_WITH_LOCATION_AND_DATE);
        matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
        matcher.addURI(authority, WeatherContract.PATH_LOCATION + "/#", LOCATION_ID);

        return matcher;
    }
}
