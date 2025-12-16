package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private TextView tvCityName, tvTemperature, tvWeatherDescription, tvTempRange, tvFeelsLike, tvUpdateTime;
    private TextView tvPressureValue, tvVisibilityValue, tvSunrise, tvSunset, tvMoonPhase, tvMoonTimes;
    private TextView tvAqiValue, tvAqiLevel, tvAqiDescription;
    private TextView tvPollenTree, tvPollenGrass, tvPollenRagweed;
    private TextView tvUvValue, tvUvLevel, tvUvDescription, tvHumidityValue, tvHumidityDescription;
    private TextView tvWindSpeed, tvWindDirection, tvDewPointValue, tvDewPointDescription;
    private TextView tvRunningStatus, tvRunningDescription;
    private ImageView ivWeatherIcon, ivWindDirectionArrow, ivRefresh;
    private View weatherHeader;
    private RecyclerView rvHourlyForecast;
    private LinearLayout llDailyContainer;
    private CityManager cityManager;
    private FloatingActionButton fabLocation;
    private ProgressBar progressBar;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String API_KEY = "9e8a5bed949526c8c672d5ae11bc567f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initManagers();
        setupListeners();
        setupWeatherHeaderAnimation();
        checkLocationPermission();
    }

    private void initViews() {
        tvCityName = findViewById(R.id.tv_city_name);
        tvTemperature = findViewById(R.id.tv_temperature);
        tvWeatherDescription = findViewById(R.id.tv_weather_description);
        tvTempRange = findViewById(R.id.tv_temp_range);
        tvFeelsLike = findViewById(R.id.tv_feels_like);
        tvUpdateTime = findViewById(R.id.tv_update_time);
        ivWeatherIcon = findViewById(R.id.iv_weather_icon);
        weatherHeader = findViewById(R.id.weather_header);
        ivRefresh = findViewById(R.id.iv_refresh);
        progressBar = findViewById(R.id.progress_bar);

        tvPressureValue = findViewById(R.id.tv_pressure_value);
        tvVisibilityValue = findViewById(R.id.tv_visibility_value);
        tvSunrise = findViewById(R.id.tv_sunrise);
        tvSunset = findViewById(R.id.tv_sunset);
        tvMoonPhase = findViewById(R.id.tv_moon_phase);
        tvMoonTimes = findViewById(R.id.tv_moon_times);

        tvAqiValue = findViewById(R.id.tv_aqi_value);
        tvAqiLevel = findViewById(R.id.tv_aqi_level);
        tvAqiDescription = findViewById(R.id.tv_aqi_description);

        tvPollenTree = findViewById(R.id.tv_pollen_tree);
        tvPollenGrass = findViewById(R.id.tv_pollen_grass);
        tvPollenRagweed = findViewById(R.id.tv_pollen_ragweed);

        tvUvValue = findViewById(R.id.tv_uv_value);
        tvUvLevel = findViewById(R.id.tv_uv_level);
        tvUvDescription = findViewById(R.id.tv_uv_description);
        tvHumidityValue = findViewById(R.id.tv_humidity_value);
        tvHumidityDescription = findViewById(R.id.tv_humidity_description);

        tvWindSpeed = findViewById(R.id.tv_wind_speed);
        tvWindDirection = findViewById(R.id.tv_wind_direction);
        tvDewPointValue = findViewById(R.id.tv_dew_point_value);
        tvDewPointDescription = findViewById(R.id.tv_dew_point_description);
        ivWindDirectionArrow = findViewById(R.id.iv_wind_direction);

        tvRunningStatus = findViewById(R.id.tv_running_status);
        tvRunningDescription = findViewById(R.id.tv_running_description);

        rvHourlyForecast = findViewById(R.id.rv_hourly_forecast);
        llDailyContainer = findViewById(R.id.ll_daily_container);
        fabLocation = findViewById(R.id.fab_location);

        rvHourlyForecast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void initManagers() {
        cityManager = CityManager.getInstance();
    }

    private void setupListeners() {
        fabLocation.setOnClickListener(v -> {
            Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
            fabLocation.startAnimation(pulse);
            showCitySearchDialog();
        });

        ivRefresh.setOnClickListener(v -> {
            rotateRefreshIcon();
            updateWeatherData();
        });

        findViewById(R.id.iv_precipitation_map).setOnClickListener(v -> {
            Toast.makeText(this, "Карта осадков будет реализована", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupWeatherHeaderAnimation() {
        findViewById(R.id.main_coordinator).setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            float translationY = -scrollY * 0.5f;
            weatherHeader.setTranslationY(translationY);
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Разрешение на геолокацию")
                        .setMessage("Для определения погоды в вашем местоположении требуется доступ к геолокации")
                        .setPositiveButton("Разрешить", (dialog, which) -> ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE))
                        .setNegativeButton("Позже", (dialog, which) -> setDefaultCity())
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                setDefaultCity();
            }
        }
    }

    private void getCurrentLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                findNearestCity(location.getLatitude(), location.getLongitude());
                            } else {
                                setDefaultCity();
                            }
                        })
                        .addOnFailureListener(e -> setDefaultCity());
            }
        } catch (Exception e) {
            setDefaultCity();
        }
    }

    private void findNearestCity(double latitude, double longitude) {
        City nearestCity = null;
        double minDistance = Double.MAX_VALUE;
        for (City city : cityManager.getAllCities()) {
            double distance = calculateDistance(latitude, longitude, city.getLatitude(), city.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                nearestCity = city;
            }
        }
        if (nearestCity != null) {
            cityManager.setCurrentCity(nearestCity);
            updateUIWithCurrentCity();
            Toast.makeText(this, "Определено местоположение: " + nearestCity.getName(), Toast.LENGTH_SHORT).show();
        } else {
            setDefaultCity();
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    private void setDefaultCity() {
        City moscow = new City("Москва", 55.7558, 37.6173, true);
        cityManager.setCurrentCity(moscow);
        updateUIWithCurrentCity();
    }

    private void updateWeatherData() {
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.3f);
        fadeOut.setDuration(300);
        AlphaAnimation fadeIn = new AlphaAnimation(0.3f, 1.0f);
        fadeIn.setDuration(300);
        weatherHeader.startAnimation(fadeOut);
        new Handler().postDelayed(() -> {
            updateUIWithCurrentCity();
            weatherHeader.startAnimation(fadeIn);
            Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show();
        }, 300);
    }

    private void updateUIWithCurrentCity() {
        City currentCity = cityManager.getCurrentCity();
        tvCityName.setText(currentCity.getName());
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        tvCityName.startAnimation(slideIn);
        SimpleDateFormat sdf = new SimpleDateFormat("E, HH:mm", new Locale("ru"));
        tvUpdateTime.setText("Обновление... " + sdf.format(new Date()));
        loadWeatherData(currentCity.getName());
    }

    private void loadWeatherData(String cityName) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            HttpURLConnection currentConnection = null;
            HttpURLConnection forecastConnection = null;
            try {
                String currentUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&units=metric&lang=ru&appid=" + API_KEY;
                URL currentUrlObj = new URL(currentUrl);
                currentConnection = (HttpURLConnection) currentUrlObj.openConnection();
                currentConnection.setRequestMethod("GET");
                BufferedReader currentIn = new BufferedReader(new InputStreamReader(currentConnection.getInputStream()));
                String currentInputLine;
                StringBuilder currentContent = new StringBuilder();
                while ((currentInputLine = currentIn.readLine()) != null) currentContent.append(currentInputLine);
                currentIn.close();
                JSONObject currentJson = new JSONObject(currentContent.toString());

                String forecastUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&units=metric&cnt=40&appid=" + API_KEY;
                URL forecastUrlObj = new URL(forecastUrl);
                forecastConnection = (HttpURLConnection) forecastUrlObj.openConnection();
                forecastConnection.setRequestMethod("GET");
                BufferedReader forecastIn = new BufferedReader(new InputStreamReader(forecastConnection.getInputStream()));
                String forecastInputLine;
                StringBuilder forecastContent = new StringBuilder();
                while ((forecastInputLine = forecastIn.readLine()) != null) forecastContent.append(forecastInputLine);
                forecastIn.close();
                JSONObject forecastJson = new JSONObject(forecastContent.toString());

                runOnUiThread(() -> {
                    updateUIWithRealData(currentJson);
                    updateForecastData(forecastJson);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    updateHourlyForecast();
                    updateDailyForecast();
                });
                e.printStackTrace();
            } finally {
                if (currentConnection != null) currentConnection.disconnect();
                if (forecastConnection != null) forecastConnection.disconnect();
                runOnUiThread(() -> { if (progressBar != null) progressBar.setVisibility(View.GONE); });
            }
        }).start();
    }

    private void updateUIWithRealData(JSONObject jsonResponse) {
        try {
            String city = jsonResponse.getString("name");
            JSONObject main = jsonResponse.getJSONObject("main");
            double temp = main.getDouble("temp");
            double feelsLike = main.getDouble("feels_like");
            double tempMin = main.getDouble("temp_min");
            double tempMax = main.getDouble("temp_max");
            int pressure = main.getInt("pressure");
            int humidity = main.getInt("humidity");
            JSONArray weatherArray = jsonResponse.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            String description = weather.getString("description");
            String iconCode = weather.getString("icon");
            JSONObject wind = jsonResponse.getJSONObject("wind");
            double windSpeed = wind.getDouble("speed");
            double windDeg = wind.optDouble("deg", 0);
            JSONObject sys = jsonResponse.getJSONObject("sys");
            long sunrise = sys.getLong("sunrise") * 1000;
            long sunset = sys.getLong("sunset") * 1000;
            int visibility = jsonResponse.optInt("visibility", 10000) / 1000;

            tvCityName.setText(city);
            animateTemperatureChange(tvTemperature, String.format(Locale.getDefault(), "%.0f°", temp));
            tvWeatherDescription.setText(capitalizeFirstLetter(description));
            tvTempRange.setText(String.format(Locale.getDefault(), "↑ %.0f° / ↓ %.0f°", tempMax, tempMin));
            tvFeelsLike.setText(String.format(Locale.getDefault(), "Ощущается как %.0f°", feelsLike));
            setWeatherIcon(iconCode);
            tvPressureValue.setText(String.valueOf(pressure));
            tvHumidityValue.setText(humidity + "%");
            tvVisibilityValue.setText(String.format(Locale.getDefault(), "%.1f", (double) visibility));
            tvWindSpeed.setText(String.format(Locale.getDefault(), "%.1f м/с", windSpeed));
            tvWindDirection.setText(getWindDirection(windDeg));
            rotateWindDirection((float) windDeg);
            tvSunrise.setText(formatTime(sunrise));
            tvSunset.setText(formatTime(sunset));

            double dewPoint = calculateDewPoint(temp, humidity);
            tvDewPointValue.setText(String.format(Locale.getDefault(), "%.0f°", dewPoint));
            tvDewPointDescription.setText(getDewPointDescription(dewPoint));

            int uvIndex = calculateUVIndex(temp, humidity);
            tvUvValue.setText(String.valueOf(uvIndex));
            tvUvLevel.setText(getUVLevel(uvIndex));
            tvUvDescription.setText(getUVDescription(uvIndex));

            tvHumidityDescription.setText(getHumidityDescription(humidity));

            boolean isGoodForRunning = temp > 5 && temp < 30 && humidity < 80 && windSpeed < 10;
            tvRunningStatus.setText(isGoodForRunning ? "ХОРОШАЯ" : "ПЛОХАЯ");
            tvRunningDescription.setText(isGoodForRunning ? "Хорошая погода для бега" : "Плохая погода для активности 'бег'");

            SimpleDateFormat sdf = new SimpleDateFormat("E, HH:mm", new Locale("ru"));
            tvUpdateTime.setText("Обновлено: " + sdf.format(new Date()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateForecastData(JSONObject forecastJson) {
        try {
            JSONArray list = forecastJson.getJSONArray("list");
            List<HourlyForecast> hourlyData = new ArrayList<>();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Map<Long, HourlyForecast> forecastMap = new HashMap<>();

            for (int i = 0; i < Math.min(8, list.length()); i++) {
                JSONObject item = list.getJSONObject(i);
                JSONObject main = item.getJSONObject("main");
                JSONArray weatherArray = item.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);

                long timestamp = item.getLong("dt") * 1000;
                Date date = new Date(timestamp);

                HourlyForecast forecast = new HourlyForecast();
                forecast.time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
                forecast.temp = main.getDouble("temp");
                forecast.precipitation = weather.optDouble("pop", 0) * 100;
                forecast.iconCode = weather.getString("icon");

                forecastMap.put(timestamp, forecast);
            }

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            for (int hour = 0; hour < 24; hour++) {
                long targetTime = calendar.getTimeInMillis();

                long prevTime = 0, nextTime = 0;
                HourlyForecast prevForecast = null, nextForecast = null;

                for (Map.Entry<Long, HourlyForecast> entry : forecastMap.entrySet()) {
                    if (entry.getKey() <= targetTime && entry.getKey() > prevTime) {
                        prevTime = entry.getKey();
                        prevForecast = entry.getValue();
                    }
                    if (entry.getKey() >= targetTime && (nextTime == 0 || entry.getKey() < nextTime)) {
                        nextTime = entry.getKey();
                        nextForecast = entry.getValue();
                    }
                }

                if (prevForecast != null && nextForecast != null && prevTime != nextTime) {
                    double factor = (double)(targetTime - prevTime) / (nextTime - prevTime);

                    HourlyForecast interpolated = new HourlyForecast();
                    interpolated.time = timeFormat.format(new Date(targetTime));
                    interpolated.temp = prevForecast.temp + (nextForecast.temp - prevForecast.temp) * factor;
                    interpolated.precipitation = prevForecast.precipitation + (nextForecast.precipitation - prevForecast.precipitation) * factor;
                    interpolated.iconCode = targetTime - prevTime < nextTime - targetTime ? prevForecast.iconCode : nextForecast.iconCode;

                    hourlyData.add(interpolated);
                }
                else if (prevForecast != null) {
                    HourlyForecast forecast = new HourlyForecast();
                    forecast.time = timeFormat.format(new Date(targetTime));
                    forecast.temp = prevForecast.temp;
                    forecast.precipitation = prevForecast.precipitation;
                    forecast.iconCode = prevForecast.iconCode;
                    hourlyData.add(forecast);
                }

                calendar.add(Calendar.HOUR_OF_DAY, 1);
            }

            updateHourlyForecastWithRealData(hourlyData);

            Map<String, DailyForecast> dailyMap = new LinkedHashMap<>();
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            SimpleDateFormat shortDayFormat = new SimpleDateFormat("E", Locale.getDefault());

            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                JSONObject main = item.getJSONObject("main");
                JSONArray weatherArray = item.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);

                long timestamp = item.getLong("dt") * 1000;
                Date date = new Date(timestamp);
                String dayKey = dayFormat.format(date);

                if (!dailyMap.containsKey(dayKey)) {
                    DailyForecast daily = new DailyForecast();
                    daily.dayName = i == 0 ? "СЕГОДНЯ" : shortDayFormat.format(date).toUpperCase(Locale.getDefault());
                    daily.tempDay = main.getDouble("temp_max");
                    daily.tempNight = main.getDouble("temp_min");
                    daily.precipitation = weather.optDouble("pop", 0) * 100;
                    daily.iconCode = weather.getString("icon");
                    dailyMap.put(dayKey, daily);
                } else {
                    DailyForecast daily = dailyMap.get(dayKey);
                    daily.tempDay = Math.max(daily.tempDay, main.getDouble("temp_max"));
                    daily.tempNight = Math.min(daily.tempNight, main.getDouble("temp_min"));
                }
            }

            List<DailyForecast> dailyData = new ArrayList<>();
            int dailyCount = 0;
            for (Map.Entry<String, DailyForecast> entry : dailyMap.entrySet()) {
                if (dailyCount >= 7) break;
                dailyData.add(entry.getValue());
                dailyCount++;
            }

            updateDailyForecastWithRealData(dailyData);

        } catch (Exception e) {
            e.printStackTrace();
            updateHourlyForecast();
            updateDailyForecast();
        }
    }

    private void updateHourlyForecastWithRealData(List<HourlyForecast> hourlyData) {
        HourlyForecastAdapter adapter = new HourlyForecastAdapter(hourlyData);
        rvHourlyForecast.setAdapter(adapter);
    }

    private void updateDailyForecastWithRealData(List<DailyForecast> dailyData) {
        llDailyContainer.removeAllViews();

        for (DailyForecast forecast : dailyData) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_daily, llDailyContainer, false);

            TextView tvDay = itemView.findViewById(R.id.tv_day);
            TextView tvPrecipitation = itemView.findViewById(R.id.tv_precipitation);
            TextView tvTempDay = itemView.findViewById(R.id.tv_temp_day);
            TextView tvTempNight = itemView.findViewById(R.id.tv_temp_night);
            ImageView ivIcon = itemView.findViewById(R.id.iv_icon);

            tvDay.setText(forecast.dayName);
            tvPrecipitation.setText(String.format(Locale.getDefault(), "%.0f%%", forecast.precipitation));
            tvTempDay.setText(String.format(Locale.getDefault(), "%.0f°", forecast.tempDay));
            tvTempNight.setText(String.format(Locale.getDefault(), "%.0f°", forecast.tempNight));

            setForecastIcon(ivIcon, forecast.iconCode);

            llDailyContainer.addView(itemView);
        }
    }

    private void setForecastIcon(ImageView imageView, String iconCode) {
        switch (iconCode) {
            case "01d": case "01n": imageView.setImageResource(R.drawable.ic_sun); break;
            case "02d": case "02n": case "03d": case "03n": case "04d": case "04n":
                imageView.setImageResource(R.drawable.ic_cloudy); break;
            case "09d": case "09n": case "10d": case "10n": imageView.setImageResource(R.drawable.ic_rain); break;
            case "11d": case "11n": case "13d": case "13n": imageView.setImageResource(R.drawable.ic_snow); break;
            default: imageView.setImageResource(R.drawable.ic_cloud); break;
        }
    }

    private void animateTemperatureChange(TextView textView, String newValue) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(500);
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            textView.setScaleX(1 + value * 0.1f);
            textView.setScaleY(1 + value * 0.1f);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                textView.setText(newValue);
            }
        });
        animator.start();
    }

    private void rotateWindDirection(float degrees) {
        ivWindDirectionArrow.animate().rotation(degrees).setDuration(500).start();
    }

    private void rotateRefreshIcon() {
        ivRefresh.animate().rotationBy(360).setDuration(500).start();
    }

    private void updateHourlyForecast() {
        List<HourlyForecast> hourlyData = new ArrayList<>();
        String[] times = {"19:00", "20:00", "21:00", "22:00", "23:00", "00:00"};
        double[] temps = {-2, -2, -2, -3, -3, -3};
        int[] precipitations = {1, 1, 1, 1, 1, 1};
        for (int i = 0; i < times.length; i++) {
            HourlyForecast forecast = new HourlyForecast();
            forecast.time = times[i];
            forecast.temp = temps[i];
            forecast.precipitation = precipitations[i];
            hourlyData.add(forecast);
        }
        HourlyForecastAdapter adapter = new HourlyForecastAdapter(hourlyData);
        rvHourlyForecast.setAdapter(adapter);
    }

    private void updateDailyForecast() {
        llDailyContainer.removeAllViews();
        String[] days = {"СЕГОДНЯ", "ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ"};
        int[] tempsDay = {-1, -1, -1, -2, -3, -4, -3};
        int[] tempsNight = {-6, -5, -4, -2, -1, 0, -1};
        int[] precipitations = {4, 4, 8, 5, 12, 19, 7};
        for (int i = 0; i < days.length; i++) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_daily, llDailyContainer, false);
            TextView tvDay = itemView.findViewById(R.id.tv_day);
            TextView tvPrecipitation = itemView.findViewById(R.id.tv_precipitation);
            TextView tvTempDay = itemView.findViewById(R.id.tv_temp_day);
            TextView tvTempNight = itemView.findViewById(R.id.tv_temp_night);
            tvDay.setText(days[i]);
            tvPrecipitation.setText(precipitations[i] + "%");
            tvTempDay.setText(tempsDay[i] + "°");
            tvTempNight.setText(tempsNight[i] + "°");
            llDailyContainer.addView(itemView);
        }
    }

    private void showCitySearchDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_search_city, null);
        androidx.appcompat.widget.SearchView searchView = dialogView.findViewById(R.id.search_view);
        RecyclerView rvCitySearch = dialogView.findViewById(R.id.rv_city_search);
        rvCitySearch.setLayoutManager(new LinearLayoutManager(this));
        List<City> allCities = cityManager.getAllCities();
        CitySearchAdapter adapter = new CitySearchAdapter(allCities, city -> {
            cityManager.setCurrentCity(city);
            updateUIWithCurrentCity();
            dialog.dismiss();
        });
        rvCitySearch.setAdapter(adapter);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                List<City> filteredCities = cityManager.searchCities(newText);
                adapter.updateCities(filteredCities);
                return true;
            }
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private String getWindDirection(double degrees) {
        if (degrees >= 337.5 || degrees < 22.5) return "С";
        if (degrees >= 22.5 && degrees < 67.5) return "СВ";
        if (degrees >= 67.5 && degrees < 112.5) return "В";
        if (degrees >= 112.5 && degrees < 157.5) return "ЮВ";
        if (degrees >= 157.5 && degrees < 202.5) return "Ю";
        if (degrees >= 202.5 && degrees < 247.5) return "ЮЗ";
        if (degrees >= 247.5 && degrees < 292.5) return "З";
        return "СЗ";
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent serviceIntent = new Intent(this, WeatherNotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
    private void setWeatherIcon(String iconCode) {
        switch (iconCode) {
            case "01d": case "01n": ivWeatherIcon.setImageResource(R.drawable.ic_sun); break;
            case "02d": case "02n": case "03d": case "03n": case "04d": case "04n":
                ivWeatherIcon.setImageResource(R.drawable.ic_cloudy); break;
            case "09d": case "09n": case "10d": case "10n": ivWeatherIcon.setImageResource(R.drawable.ic_rain); break;
            case "11d": case "11n": ivWeatherIcon.setImageResource(R.drawable.ic_snow); break;
            case "13d": case "13n": ivWeatherIcon.setImageResource(R.drawable.ic_snow); break;
            case "50d": case "50n": ivWeatherIcon.setImageResource(R.drawable.ic_cloud); break;
            default: ivWeatherIcon.setImageResource(R.drawable.ic_cloud); break;
        }
    }

    private double calculateDewPoint(double temp, double humidity) {
        double a = 17.27;
        double b = 237.7;
        double alpha = ((a * temp) / (b + temp)) + Math.log(humidity / 100.0);
        return (b * alpha) / (a - alpha);
    }

    private String getDewPointDescription(double dewPoint) {
        if (dewPoint < 10) return "Воздух сухой";
        if (dewPoint < 16) return "Комфортная влажность";
        if (dewPoint < 20) return "Воздух влажный";
        return "Воздух очень влажный";
    }

    private int calculateUVIndex(double temp, double humidity) {
        return (int) Math.min(11, Math.max(0, (temp - 10) / 3));
    }

    private String getUVLevel(int uvIndex) {
        if (uvIndex <= 2) return "Низкий";
        if (uvIndex <= 5) return "Умеренный";
        if (uvIndex <= 7) return "Высокий";
        if (uvIndex <= 10) return "Очень высокий";
        return "Экстремальный";
    }

    private String getUVDescription(int uvIndex) {
        if (uvIndex <= 2) return "Низкий до конца дня";
        if (uvIndex <= 5) return "Умеренный, нужна защита";
        if (uvIndex <= 7) return "Высокий, нужна защита";
        if (uvIndex <= 10) return "Очень высокий, осторожно";
        return "Экстремальный, опасно";
    }

    private String getHumidityDescription(int humidity) {
        if (humidity < 30) return "Очень сухо";
        if (humidity < 50) return "Сухо";
        if (humidity < 70) return "Комфортная";
        if (humidity < 90) return "Влажно";
        return "Очень влажно";
    }

    private class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder> {
        private List<HourlyForecast> data;
        public HourlyForecastAdapter(List<HourlyForecast> data) { this.data = data; }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hourly, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            HourlyForecast forecast = data.get(position);
            holder.tvTime.setText(forecast.time);
            holder.tvTemp.setText(String.format(Locale.getDefault(), "%.0f°", forecast.temp));
            holder.tvPrecipitation.setText(String.format(Locale.getDefault(), "%.0f%%", forecast.precipitation));
            setForecastIcon(holder.ivIcon, forecast.iconCode);
        }
        @Override
        public int getItemCount() { return data.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTime, tvTemp, tvPrecipitation;
            ImageView ivIcon;
            ViewHolder(View itemView) {
                super(itemView);
                tvTime = itemView.findViewById(R.id.tv_time);
                tvTemp = itemView.findViewById(R.id.tv_temp);
                tvPrecipitation = itemView.findViewById(R.id.tv_precipitation);
                ivIcon = itemView.findViewById(R.id.iv_icon);
            }
        }
    }

    private static class CitySearchAdapter extends RecyclerView.Adapter<CitySearchAdapter.ViewHolder> {
        private List<City> cities;
        private CityClickListener listener;
        public CitySearchAdapter(List<City> cities, CityClickListener listener) {
            this.cities = cities;
            this.listener = listener;
        }
        public void updateCities(List<City> cities) {
            this.cities = cities;
            notifyDataSetChanged();
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            City city = cities.get(position);
            holder.textView.setText(city.getName());
            if (city.isCurrent()) {
                holder.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
            } else {
                holder.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            holder.itemView.setOnClickListener(v -> listener.onCityClicked(city));
        }
        @Override
        public int getItemCount() { return cities.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
        interface CityClickListener {
            void onCityClicked(City city);
        }
    }

    static class HourlyForecast {
        String time;
        double temp;
        double precipitation;
        String iconCode;
    }

    static class DailyForecast {
        String dayName;
        double tempDay;
        double tempNight;
        double precipitation;
        String iconCode;
    }
}