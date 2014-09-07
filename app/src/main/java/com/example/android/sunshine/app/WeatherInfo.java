package com.example.android.sunshine.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;

/**
 * Created by joaofernandes on 29/7/14.
 * todo: implement different methods to format date, temperature, and description
 */
public class WeatherInfo {
    private Calendar date;
    private double min_temperature;
    private double max_temperature;
    private String description;
    private Units units;

    public WeatherInfo(Calendar date, double min_tmp, double max_tmp, String description, Units units) {
        this.date = date;
        this.min_temperature = min_tmp;
        this.max_temperature = max_tmp;
        this.description = description;
        this.units = units;
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(new SimpleDateFormat("EE").format(date.getTime()))
                    .append(" - ").append(description)
                    .append(" (").append(formatTemperature(min_temperature))
                    .append("/").append(formatTemperature(max_temperature))
                    .append(")");

            return builder.toString();
        } catch (Exception e) {
            return "";
        }
    }
    public void setUnits(Units units) {
        this.units = units;
    }

    private String formatTemperature(double tmp) {


        if (units == Units.IMPERIAL) {
            tmp = tmp * 1.8 + 32;
        }
        return new Formatter().format("%.1f", tmp).toString();
    }

}
