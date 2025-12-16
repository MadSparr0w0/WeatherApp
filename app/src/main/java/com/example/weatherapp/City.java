package com.example.weatherapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "cities") // Указываем имя таблицы в БД
public class City {
    @PrimaryKey(autoGenerate = true) // ID будет генерироваться автоматически
    private int id;

    @ColumnInfo(name = "city_name")
    private String name;

    private double latitude;
    private double longitude;

    @ColumnInfo(name = "is_current")
    private boolean isCurrent;

    // Конструктор (Room также может использовать другие)
    public City(String name, double latitude, double longitude, boolean isCurrent) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isCurrent = isCurrent;
    }

    // Геттеры и сеттеры для ВСЕХ полей (включая id)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public boolean isCurrent() { return isCurrent; }
    public void setCurrent(boolean current) { isCurrent = current; }
}