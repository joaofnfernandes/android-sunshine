package com.example.android.sunshine.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.sunshine.app.data.WeatherContract.*;

/**
 * Created by joaofernandes on 24/8/14.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = WeatherDbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "weather.db";


    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create table to hold locations

        final String SQL_CREATE_LOCATION_TABLE =
                new StringBuilder()
                        .append("CREATE TABLE ")
                        .append(LocationEntry.TABLE_NAME)
                        .append(" (")
                        .append(LocationEntry._ID).append(" INTEGER PRIMARY KEY, ")
                        .append(LocationEntry.COLUMN_LOCATION_SETTING).append(" TEXT UNIQUE NOT NULL, ")
                        .append(LocationEntry.COLUMN_CITY_NAME).append(" TEXT NOT NULL, ")
                        .append(LocationEntry.COLUMN_COORD_LAT).append(" REAL NOT NULL, ")
                        .append(LocationEntry.COLUMN_COORD_LONG).append(" REAL NOT NULL, ")
                        .append("UNIQUE (").append(LocationEntry.COLUMN_LOCATION_SETTING).append(") ")
                        .append("ON CONFLICT IGNORE")
                        .append(");")
                        .toString();

        Log.d(LOG_TAG, SQL_CREATE_LOCATION_TABLE);


        final String SQL_CREATE_WEATHER_TABLE =
                new StringBuilder()
                    .append("CREATE TABLE ")
                    .append(WeatherEntry.TABLE_NAME)
                    .append(" (")
                        .append(WeatherEntry._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")

                        .append(WeatherEntry.COLUMN_LOC_KEY).append(" INTEGER NOT NULL, ")

                        .append(WeatherEntry.COLUMN_DATETEXT).append(" TEXT NOT NULL, ")
                        .append(WeatherEntry.COLUMN_SHORT_DESC).append(" TEXT NOT NULL, ")
                        .append(WeatherEntry.COLUMN_WEATHER_ID).append(" INTEGER NOT NULL, ")

                        .append(WeatherEntry.COLUMN_MIN_TEMP).append(" REAL NOT NULL, ")
                        .append(WeatherEntry.COLUMN_MAX_TEMP).append(" REAL NOT NULL, ")

                        .append(WeatherEntry.COLUMN_HUMIDITY).append(" REAL NOT NULL, ")
                        .append(WeatherEntry.COLUMN_PRESSURE).append(" REAL NOT NULL, ")
                        .append(WeatherEntry.COLUMN_WIND_SPEED).append(" REAL NOT NULL, ")
                        .append(WeatherEntry.COLUMN_DEGREES).append(" REAL NOT NULL, ")

                        .append(" FOREIGN KEY (")
                            .append(WeatherEntry.COLUMN_LOC_KEY)
                        .append(" ) ")
                        .append(" REFERENCES ")
                            .append(LocationEntry.TABLE_NAME)
                            .append(" (").append(LocationEntry._ID).append("), ")
                        .append(" UNIQUE (")
                            .append(WeatherEntry.COLUMN_DATETEXT)
                            .append(", ")
                            .append(WeatherEntry.COLUMN_LOC_KEY)
                        .append(") ")
                        .append("ON CONFLICT REPLACE")
                    .append(");")
                    .toString();

        Log.d(LOG_TAG, SQL_CREATE_WEATHER_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        final String SQL_DROP_LOCATIONS = "DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME ;
        final String SQL_DROP_WEATHER = "DROP TABLE IF EXISTS" +  WeatherEntry.TABLE_NAME;

        sqLiteDatabase.execSQL(SQL_DROP_LOCATIONS);
        sqLiteDatabase.execSQL(SQL_DROP_WEATHER);
        onCreate(sqLiteDatabase);

    }
}
