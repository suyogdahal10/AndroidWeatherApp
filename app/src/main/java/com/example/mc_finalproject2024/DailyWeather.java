package com.example.mc_finalproject2024;

public class DailyWeather {
    private String date;
    private double maxTemp;
    private double minTemp;
    private String condition;

    public DailyWeather(String date, double maxTemp, double minTemp, String condition) {
        this.date = date;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.condition = condition;
    }

    public String getDate() {
        return date;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public String getCondition() {
        return condition;
    }
}