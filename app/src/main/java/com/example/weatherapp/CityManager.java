package com.example.weatherapp;

import java.util.ArrayList;
import java.util.List;

public class CityManager {
    private List<City> cities = new ArrayList<>();
    private static CityManager instance;

    private CityManager() {
        cities.add(new City("Батайск", 47.1397, 39.7518, true));
        cities.add(new City("Москва", 55.7558, 37.6173, false));
        cities.add(new City("Санкт-Петербург", 59.9343, 30.3351, false));
        cities.add(new City("Новосибирск", 55.0084, 82.9357, false));
        cities.add(new City("Екатеринбург", 56.8389, 60.6057, false));
        cities.add(new City("Нижний Новгород", 56.3269, 44.0065, false));
        cities.add(new City("Казань", 55.7964, 49.1089, false));
        cities.add(new City("Челябинск", 55.1644, 61.4368, false));
        cities.add(new City("Омск", 54.9924, 73.3686, false));
        cities.add(new City("Самара", 53.1959, 50.1002, false));
        cities.add(new City("Ростов-на-Дону", 47.2225, 39.7188, false));
        cities.add(new City("Уфа", 54.7351, 55.9587, false));
        cities.add(new City("Красноярск", 56.0153, 92.8932, false));
        cities.add(new City("Воронеж", 51.6720, 39.1843, false));
        cities.add(new City("Пермь", 58.0105, 56.2294, false));
        cities.add(new City("Волгоград", 48.7080, 44.5133, false));
        cities.add(new City("Краснодар", 45.0355, 38.9753, false));
        cities.add(new City("Саратов", 51.5336, 46.0343, false));
        cities.add(new City("Тюмень", 57.1530, 65.5343, false));
        cities.add(new City("Тольятти", 53.5088, 49.4192, false));
        cities.add(new City("Ижевск", 56.8527, 53.2115, false));
        cities.add(new City("Барнаул", 53.3477, 83.7798, false));
        cities.add(new City("Ульяновск", 54.3080, 48.3749, false));
        cities.add(new City("Иркутск", 52.2864, 104.2807, false));
        cities.add(new City("Хабаровск", 48.4802, 135.0719, false));
        cities.add(new City("Ярославль", 57.6261, 39.8845, false));
        cities.add(new City("Владивосток", 43.1155, 131.8855, false));
        cities.add(new City("Махачкала", 42.9849, 47.5048, false));
        cities.add(new City("Томск", 56.4977, 84.9744, false));
        cities.add(new City("Оренбург", 51.7682, 55.0974, false));
        cities.add(new City("Кемерово", 55.3547, 86.0873, false));
        cities.add(new City("Новокузнецк", 53.7865, 87.1552, false));
        cities.add(new City("Рязань", 54.6294, 39.7396, false));
        cities.add(new City("Астрахань", 46.3479, 48.0336, false));
        cities.add(new City("Набережные Челны", 55.7436, 52.3959, false));
        cities.add(new City("Пенза", 53.1951, 45.0183, false));
        cities.add(new City("Липецк", 52.6088, 39.5992, false));
        cities.add(new City("Киров", 58.6036, 49.6680, false));
        cities.add(new City("Чебоксары", 56.1463, 47.2511, false));
        cities.add(new City("Тула", 54.1931, 37.6173, false));
        cities.add(new City("Калининград", 54.7104, 20.4522, false));
        cities.add(new City("Брянск", 53.2436, 34.3642, false));
        cities.add(new City("Курск", 51.7304, 36.1926, false));
        cities.add(new City("Магнитогорск", 53.4117, 58.9844, false));
        cities.add(new City("Тверь", 56.8584, 35.9000, false));
        cities.add(new City("Ставрополь", 45.0445, 41.9691, false));
        cities.add(new City("Севастополь", 44.6166, 33.5254, false));
        cities.add(new City("Симферополь", 44.9521, 34.1024, false));
    }

    public static CityManager getInstance() {
        if (instance == null) {
            instance = new CityManager();
        }
        return instance;
    }

    public List<City> getAllCities() {
        return new ArrayList<>(cities);
    }

    public List<City> searchCities(String query) {
        List<City> result = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (City city : cities) {
            if (city.getName().toLowerCase().contains(lowerQuery)) {
                result.add(city);
            }
        }
        return result;
    }

    public City getCurrentCity() {
        for (City city : cities) {
            if (city.isCurrent()) {
                return city;
            }
        }
        return cities.get(0);
    }

    public void setCurrentCity(City selectedCity) {
        for (City city : cities) {
            city.setCurrent(false);
        }
        for (City city : cities) {
            if (city.getName().equals(selectedCity.getName()) &&
                    city.getLatitude() == selectedCity.getLatitude()) {
                city.setCurrent(true);
                break;
            }
        }
    }

    public void addCity(City city) {
        cities.add(city);
    }

    public void removeCity(City city) {
        cities.removeIf(c ->
                c.getName().equals(city.getName()) &&
                        c.getLatitude() == city.getLatitude() &&
                        c.getLongitude() == city.getLongitude()
        );
    }
}