package com.geekbrains.weather.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WeatherTable {
    private final static String TABLE_NAME = "weather_info";
    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_CITY = "city";
    private final static String COLUMN_TEMP = "temperature";
    private final static String COLUMN_HUMIDITY = "humidity";
    private final static String COLUMN_SPEED = "wind_speed";
    private final static String COLUMN_PRESSURE = "pressure";
    private final static String COLUMN_COUNTRY = "country";
    private final static String COLUMN_TITLE = "title";


    static void createTable(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CITY + " TEXT, "
                + COLUMN_TEMP + " REAL, " + COLUMN_HUMIDITY + " INTEGER, "
                + COLUMN_SPEED + " REAL, " + COLUMN_PRESSURE + " INTEGER, "
                + COLUMN_COUNTRY + " TEXT);");
    }

    static void onUpgrade(SQLiteDatabase database) {
        database.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_TITLE
                + " TEXT DEFAULT 'Default title'");
    }

    public static void add(String city,
                           float temp,
                           int humidity,
                           float windSpeed,
                           int pressure,
                           String country,
                           SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY, city);
        values.put(COLUMN_TEMP, temp);
        values.put(COLUMN_HUMIDITY, humidity);
        values.put(COLUMN_SPEED, windSpeed);
        values.put(COLUMN_PRESSURE, pressure);
        values.put(COLUMN_COUNTRY, country);


        database.insert(TABLE_NAME, null, values);
    }

    public static void edit(String city,
                            float temp,
                            int humidity,
                            float windSpeed,
                            int pressure,
                            String country,
                            SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEMP, temp);
        values.put(COLUMN_HUMIDITY, humidity);
        values.put(COLUMN_SPEED, windSpeed);
        values.put(COLUMN_PRESSURE, pressure);
        values.put(COLUMN_COUNTRY, country);

        database.update(TABLE_NAME, values, COLUMN_CITY + "=?", new String[]{city});
    }

    public static float getTemp(String city, SQLiteDatabase database) {
        @SuppressLint("Recycle") Cursor cursor = database.query(TABLE_NAME, new String[]{COLUMN_CITY, COLUMN_TEMP}, null, null,
                null, null, null);
        return getTempFromCursor(city, cursor);
    }

    public static int getHumidity(String city, SQLiteDatabase database) {
        @SuppressLint("Recycle") Cursor cursor = database.query(TABLE_NAME, new String[]{COLUMN_CITY, COLUMN_HUMIDITY}, null, null,
                null, null, null);
        return getHumidityFromCursor(city, cursor);
    }

    public static float getWindSpeed(String city, SQLiteDatabase database) {
        @SuppressLint("Recycle") Cursor cursor = database.query(TABLE_NAME, new String[]{COLUMN_CITY, COLUMN_SPEED}, null, null,
                null, null, null);
        return getWindSpeedFromCursor(city, cursor);
    }

    public static int getPressure(String city, SQLiteDatabase database) {
        @SuppressLint("Recycle") Cursor cursor = database.query(TABLE_NAME, new String[]{COLUMN_CITY, COLUMN_PRESSURE}, null, null,
                null, null, null);
        return getPressureFromCursor(city, cursor);
    }

    public static String getCountry(String city, SQLiteDatabase database) {
        @SuppressLint("Recycle") Cursor cursor = database.query(TABLE_NAME, new String[]{COLUMN_CITY, COLUMN_COUNTRY}, null, null,
                null, null, null);
        return getCountryFromCursor(city, cursor);
    }

    public static boolean ifExists(String city, SQLiteDatabase database) {
        @SuppressLint("Recycle") Cursor cursor = database.query(TABLE_NAME, new String[]{COLUMN_CITY}, null, null,
                null, null, null);
        return getResultFromCursor(city, cursor);
    }

    private static float getTempFromCursor(String city, Cursor cursor) {
        float result = 0;

        if(cursor != null && cursor.moveToFirst()) {

            int cityIdx = cursor.getColumnIndex(COLUMN_CITY);
            int tempIdx = cursor.getColumnIndex(COLUMN_TEMP);

            while (cursor.moveToNext()) {
                if (cursor.getString(cityIdx).equals(city)) {
                    result = cursor.getFloat(tempIdx);
                }
            }
        }
        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception ignored) {}
        return result;
    }

    private static int getHumidityFromCursor(String city, Cursor cursor) {
        int result = 0;

        if(cursor != null && cursor.moveToFirst()) {

            int cityIdx = cursor.getColumnIndex(COLUMN_CITY);
            int humidityIdx = cursor.getColumnIndex(COLUMN_HUMIDITY);

            while (cursor.moveToNext()) {
                if (cursor.getString(cityIdx).equals(city)) {
                    result = cursor.getInt(humidityIdx);
                }
            }
        }
        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception ignored) {}
        return result;
    }

    private static float getWindSpeedFromCursor(String city, Cursor cursor) {
        float result = 0;

        if(cursor != null && cursor.moveToFirst()) {

            int cityIdx = cursor.getColumnIndex(COLUMN_CITY);
            int windSpeedIdx = cursor.getColumnIndex(COLUMN_SPEED);

            while (cursor.moveToNext()) {
                if (cursor.getString(cityIdx).equals(city)) {
                    result = cursor.getFloat(windSpeedIdx);
                }
            }
        }
        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception ignored) {}
        return result;
    }

    private static int getPressureFromCursor(String city, Cursor cursor) {
        int result = 0;

        if(cursor != null && cursor.moveToFirst()) {

            int cityIdx = cursor.getColumnIndex(COLUMN_CITY);
            int pressureIdx = cursor.getColumnIndex(COLUMN_PRESSURE);

            while (cursor.moveToNext()) {
                if (cursor.getString(cityIdx).equals(city)) {
                    result = cursor.getInt(pressureIdx);
                }
            }
        }
        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception ignored) {}
        return result;
    }

    private static String getCountryFromCursor(String city, Cursor cursor) {
        String result = null;

        if(cursor != null && cursor.moveToFirst()) {

            int cityIdx = cursor.getColumnIndex(COLUMN_CITY);
            int countryIdx = cursor.getColumnIndex(COLUMN_COUNTRY);

            while (cursor.moveToNext()) {
                if (cursor.getString(cityIdx).equals(city)) {
                    result = cursor.getString(countryIdx);
                }
            }
        }
        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception ignored) {}
        return result;
    }

    private static boolean getResultFromCursor(String city, Cursor cursor) {
        boolean result = false;

        if(cursor != null && cursor.moveToFirst()) {

            int cityIdx = cursor.getColumnIndex(COLUMN_CITY);

            while (cursor.moveToNext()) {
                if (cursor.getString(cityIdx).equals(city)) {
                    result = true;
                }
            }
        }
        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception ignored) {}
        return result;
    }
}
