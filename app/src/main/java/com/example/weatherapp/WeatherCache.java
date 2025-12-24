package com.example.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WeatherCache {
    private static final String TAG = "WeatherCache";
    private static final String PREFS_NAME = "weather_cache";
    private static final long CACHE_EXPIRY_TIME = 30 * 60 * 1000; // 30 минут

    private SharedPreferences prefs;
    private Gson gson;

    public WeatherCache(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new GsonBuilder().create();
    }

    public void saveWeatherData(WeatherData weatherData) {
        try {
            String json = gson.toJson(weatherData);
            prefs.edit()
                    .putString(weatherData.getCityName(), json)
                    .putLong(weatherData.getCityName() + "_time", System.currentTimeMillis())
                    .apply();
            Log.d(TAG, "Данные сохранены в кэш для: " + weatherData.getCityName());
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при сохранении в кэш: " + e.getMessage());
        }
    }

    public WeatherData getWeatherData(String cityName) {
        try {
            long cacheTime = prefs.getLong(cityName + "_time", 0);
            long currentTime = System.currentTimeMillis();

            if (currentTime - cacheTime > CACHE_EXPIRY_TIME) {
                Log.d(TAG, "Кэш устарел для: " + cityName);
                return null;
            }

            String json = prefs.getString(cityName, null);
            if (json != null) {
                WeatherData data = gson.fromJson(json, WeatherData.class);
                data.setCacheTime(cacheTime);
                Log.d(TAG, "Данные загружены из кэша для: " + cityName);
                return data;
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при загрузке из кэша: " + e.getMessage());
        }
        return null;
    }

    public void clearCache() {
        prefs.edit().clear().apply();
        Log.d(TAG, "Кэш очищен");
    }
}