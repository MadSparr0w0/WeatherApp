package com.example.weatherapp;

public class WeatherEnums {

    public enum UVLevel {
        LOW("Низкий", "Можно находиться на солнце без защиты"),
        MODERATE("Умеренный", "Рекомендуется защита от солнца"),
        HIGH("Высокий", "Необходима защита от солнца"),
        VERY_HIGH("Очень высокий", "Опасно находиться на солнце"),
        EXTREME("Экстремальный", "Очень опасно, избегайте солнца");

        private final String displayName;
        private final String description;

        UVLevel(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }

        public static UVLevel fromUVIndex(int uvIndex) {
            if (uvIndex <= 2) return LOW;
            if (uvIndex <= 5) return MODERATE;
            if (uvIndex <= 7) return HIGH;
            if (uvIndex <= 10) return VERY_HIGH;
            return EXTREME;
        }
    }

    public enum HumidityLevel {
        VERY_DRY("Очень сухо", "Воздух очень сухой"),
        DRY("Сухо", "Воздух сухой"),
        COMFORTABLE("Комфортно", "Влажность комфортная"),
        HUMID("Влажно", "Воздух влажный"),
        VERY_HUMID("Очень влажно", "Воздух очень влажный");

        private final String displayName;
        private final String description;

        HumidityLevel(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }

        public static HumidityLevel fromHumidity(int humidity) {
            if (humidity < 30) return VERY_DRY;
            if (humidity < 50) return DRY;
            if (humidity < 70) return COMFORTABLE;
            if (humidity < 90) return HUMID;
            return VERY_HUMID;
        }
    }

    public enum AirQualityLevel {
        GOOD("Хорошо", "Качество воздуха удовлетворительное"),
        MODERATE("Удовлетворительно", "Качество воздуха приемлемое"),
        UNHEALTHY_SENSITIVE("Вредно для чувствительных групп", "Вредно для чувствительных групп"),
        UNHEALTHY("Вредно", "Вредно для здоровья"),
        VERY_UNHEALTHY("Очень вредно", "Очень вредно для здоровья"),
        HAZARDOUS("Опасно", "Опасно для здоровья");

        private final String displayName;
        private final String description;

        AirQualityLevel(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }

        public static AirQualityLevel fromAqiValue(int aqi) {
            if (aqi <= 50) return GOOD;
            if (aqi <= 100) return MODERATE;
            if (aqi <= 150) return UNHEALTHY_SENSITIVE;
            if (aqi <= 200) return UNHEALTHY;
            if (aqi <= 300) return VERY_UNHEALTHY;
            return HAZARDOUS;
        }
    }
}