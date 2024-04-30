package com.example.mc_finalproject2024;

public class LocationWeather {
    private String locationName;
    private double temperature;
    private String weatherCondition;

    public LocationWeather(String locationName, double temperature, String weatherCondition) {
        this.locationName = locationName;
        this.temperature = temperature;
        this.weatherCondition = weatherCondition;
    }

    // Getters and setters
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }
}
