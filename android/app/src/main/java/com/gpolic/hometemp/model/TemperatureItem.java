package com.gpolic.hometemp.model;

import android.support.annotation.NonNull;

import java.util.Date;

import static com.gpolic.hometemp.util.DateUtils.dateToDBString;
import static com.gpolic.hometemp.util.DateUtils.dateToString;
import static com.gpolic.hometemp.util.DateUtils.stringToDateTime;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * TemperatureItem
 * <p>
 * mDate is the date/time of this temperature reading
 * <p>mTemperature is in Celsius format
 * <p>mHumidity in percentage
 */
public class TemperatureItem {
    private Date mDate;
    private float mTemperature;
    private float mHumidity;

    public TemperatureItem() {
    }


    public TemperatureItem(@NonNull Date date, float temperature, float humidity) {
        mDate = date;
        mTemperature = temperature;
        mHumidity = humidity;
    }

    public TemperatureItem(@NonNull String date, @NonNull String temperature, @NonNull String humidity) {
        this(stringToDateTime(date), Float.valueOf(temperature), Float.valueOf(humidity));
    }

    // this is used only in Tests
    public TemperatureItem(float temperature, float humidity) {
        this(new Date(), temperature, humidity);
    }


    public static double convertCtoF(double c) {
        return c * 1.8 + 32;
    }

    public static double convertFtoC(double f) {
        return (f - 32) * 0.55555;
    }

    public float getTemperature() {
        return mTemperature;
    }

    public void setTemperature(float temperature) {
        mTemperature = temperature;
    }

    public float getHumidity() {
        return mHumidity;
    }

    public void setHumidity(float humidity) {
        mHumidity = humidity;
    }

    public String getDateString() {
        return dateToString(mDate);
    }

    public String getDateDBString() {
        return dateToDBString(mDate);
    }

    public Date getDate() {
        //return (Date) mDate.clone();    the user should not change the dates
        return mDate;
    }

    public void setDate(String stringDate) {
        mDate = stringToDateTime(stringDate);
    }

    public void setDate(Date date) {
        mDate = date;
    }


    /**
     * getHeatIndex calculates heat index on this temp and humidity item pair
     * The calculation is based on:
     * http://www.wpc.ncep.noaa.gov/html/heatindex_equation.shtml
     * <p>
     * This calculation works in Fahrenheit temperatures, so conversion is applicable with convertFtoC
     *
     * @param temperature
     * @param humidity
     * @return
     */
    private static double getHeatIndex(double temperature, double humidity) {
        double hi = 0.5 * (temperature + 61.0 + ((temperature - 68.0) * 1.2) + (humidity * 0.094));

        if (hi > 79) {
            hi = -42.379 +
                    2.04901523 * temperature +
                    10.14333127 * humidity +
                    -0.22475541 * temperature * humidity +
                    -0.00683783 * pow(temperature, 2) +
                    -0.05481717 * pow(humidity, 2) +
                    0.00122874 * pow(temperature, 2) * humidity +
                    0.00085282 * temperature * pow(humidity, 2) +
                    -0.00000199 * pow(temperature, 2) * pow(humidity, 2);

            if ((humidity < 13.0) && (temperature >= 80.0) && (temperature <= 112.0))
                hi -= ((13.0 - humidity) * 0.25) * sqrt((17.0 - abs(temperature - 95.0)) * 0.05882);

            else if ((humidity > 85.0) && (temperature >= 80.0) && (temperature <= 87.0))
                hi += ((humidity - 85.0) * 0.1) * ((87.0 - temperature) * 0.2);
        }
        return hi;
    }

    /**
     * Calculates the heat index on the item. Considering that the temperature is Celcius
     * @return The heat index calculated from the temperature and humidity
     */
    public double getHeatIndexCel() {
        final double fahrenheitTemperature = convertCtoF(mTemperature);
        final double heatIndex = getHeatIndex(fahrenheitTemperature, mHumidity);

        return convertFtoC(heatIndex);
    }

    /**
     * Calculates the heat index on the item. Considering that the temperature is Fahrenheit
     * @return The heat index calculated from the temperature and humidity
     */
    public double getHeatIndexFar() {
        return getHeatIndex(mTemperature, mHumidity);
    }

}
