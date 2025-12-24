package com.example.weatherapp;

public class WeatherData {
    private String cityName;
    private String currentWeatherJson;
    private String forecastJson;
    private long cacheTime;

    public WeatherData(String cityName, String currentWeatherJson, String forecastJson) {
        this.cityName = cityName;
        this.currentWeatherJson = currentWeatherJson;
        this.forecastJson = forecastJson;
        this.cacheTime = System.currentTimeMillis();
    }

    public String getCityName() {
        return cityName;
    }

    public String getCurrentWeatherJson() {
        return currentWeatherJson;
    }

    public String getForecastJson() {
        return forecastJson;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - cacheTime) > (30 * 60 * 1000); // 30 минут
    }
}