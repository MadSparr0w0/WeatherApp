package com.example.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CityManager {
    private static final String TAG = "CityManager";
    private static final String API_KEY = "9e8a5bed949526c8c672d5ae11bc567f";
    private static final String GEO_API_URL = "https://api.openweathermap.org/geo/1.0/direct?q=%s&limit=10&appid=%s";
    private static final String REVERSE_GEO_API_URL = "http://api.openweathermap.org/geo/1.0/reverse?lat=%f&lon=%f&limit=1&appid=%s";

    private City currentCity;
    private static CityManager instance;
    private SharedPreferences prefs;
    private Context context;

    private CityManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("city_prefs", Context.MODE_PRIVATE);
        loadCurrentCityFromPrefs();
    }

    public static synchronized CityManager getInstance(Context context) {
        if (instance == null) {
            instance = new CityManager(context);
        }
        return instance;
    }

    public void searchCitiesFromApi(String query, OnCitiesLoadedListener listener) {
        new Thread(() -> {
            try {
                String encodedQuery = URLEncoder.encode(query, "UTF-8");
                String urlString = String.format(GEO_API_URL, encodedQuery, API_KEY);
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONArray jsonArray = new JSONArray(response.toString());
                    List<City> cities = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cityJson = jsonArray.getJSONObject(i);
                        String name = cityJson.getString("name");
                        String country = cityJson.optString("country", "");
                        String state = cityJson.optString("state", "");
                        double lat = cityJson.getDouble("lat");
                        double lon = cityJson.getDouble("lon");

                        StringBuilder displayName = new StringBuilder(name);
                        if (!state.isEmpty()) {
                            displayName.append(", ").append(state);
                        }
                        if (!country.isEmpty()) {
                            displayName.append(", ").append(country);
                        }

                        City city = new City(displayName.toString(), lat, lon, false);
                        cities.add(city);
                    }

                    if (listener != null) {
                        listener.onCitiesLoaded(cities);
                    }
                } else {
                    if (listener != null) {
                        listener.onError("HTTP ошибка: " + connection.getResponseCode());
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при поиске городов: " + e.getMessage(), e);
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        }).start();
    }

    public void getCityByCoordinates(double lat, double lon, OnCityFoundListener listener) {
        new Thread(() -> {
            try {
                String urlString = String.format(REVERSE_GEO_API_URL, lat, lon, API_KEY);
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONArray jsonArray = new JSONArray(response.toString());
                    if (jsonArray.length() > 0) {
                        JSONObject cityJson = jsonArray.getJSONObject(0);
                        String name = cityJson.getString("name");
                        String country = cityJson.optString("country", "");
                        String state = cityJson.optString("state", "");
                        double latitude = cityJson.getDouble("lat");
                        double longitude = cityJson.getDouble("lon");

                        StringBuilder displayName = new StringBuilder(name);
                        if (!state.isEmpty()) {
                            displayName.append(", ").append(state);
                        }
                        if (!country.isEmpty()) {
                            displayName.append(", ").append(country);
                        }

                        City city = new City(displayName.toString(), latitude, longitude, true);
                        setCurrentCity(city);

                        if (listener != null) {
                            listener.onCityFound(city);
                        }
                    } else {
                        if (listener != null) {
                            listener.onError("Город не найден по координатам");
                        }
                    }
                } else {
                    if (listener != null) {
                        listener.onError("HTTP ошибка: " + connection.getResponseCode());
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при получении города по координатам: " + e.getMessage(), e);
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        }).start();
    }

    private void loadCurrentCityFromPrefs() {
        String name = prefs.getString("current_city_name", null);
        if (name != null) {
            double lat = Double.longBitsToDouble(prefs.getLong("current_city_lat", 0));
            double lon = Double.longBitsToDouble(prefs.getLong("current_city_lon", 0));
            currentCity = new City(name, lat, lon, true);
        }
    }

    private void saveCurrentCityToPrefs(City city) {
        if (city == null) return;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("current_city_name", city.getName());
        editor.putLong("current_city_lat", Double.doubleToRawLongBits(city.getLatitude()));
        editor.putLong("current_city_lon", Double.doubleToRawLongBits(city.getLongitude()));
        editor.apply();
    }

    public City getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(City city) {
        if (city == null) return;
        this.currentCity = city;
        saveCurrentCityToPrefs(city);
    }

    public interface OnCitiesLoadedListener {
        void onCitiesLoaded(List<City> cities);
        void onError(String error);
    }

    public interface OnCityFoundListener {
        void onCityFound(City city);
        void onError(String error);
    }
}