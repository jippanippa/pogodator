package com.meojike.pogodator.weather;

import com.meojike.pogodator.R;

public class Forecast {
    private CurrentWeather mCurrentWeather;
    private HourlyWeather[] mHourlyWeatherArray;
    private DailyWeather[] mDailyWeatherArray;

    public CurrentWeather getCurrentWeather() {
        return mCurrentWeather;
    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        mCurrentWeather = currentWeather;
    }

    public HourlyWeather[] getHourlyWeatherArray() {
        return mHourlyWeatherArray;
    }

    public void setHourlyWeatherArray(HourlyWeather[] hourlyWeatherArray) {
        mHourlyWeatherArray = hourlyWeatherArray;
    }

    public DailyWeather[] getDailyWeatherArray() {
        return mDailyWeatherArray;
    }

    public void setDailyWeatherArray(DailyWeather[] dailyWeatherArray) {
        mDailyWeatherArray = dailyWeatherArray;
    }

    public static int getIconId(String iconString) {
        int iconId = R.drawable.clear_day;

        if (iconString.equals("clear-day")) {
            iconId = R.drawable.clear_day;
        }
        else if (iconString.equals("clear-night")) {
            iconId = R.drawable.clear_night;
        }
        else if (iconString.equals("rain")) {
            iconId = R.drawable.rain;
        }
        else if (iconString.equals("snow")) {
            iconId = R.drawable.snow;
        }
        else if (iconString.equals("sleet")) {
            iconId = R.drawable.sleet;
        }
        else if (iconString.equals("wind")) {
            iconId = R.drawable.wind;
        }
        else if (iconString.equals("fog")) {
            iconId = R.drawable.fog;
        }
        else {
            iconId = R.drawable.cloudy;
        }
//        else if (iconString.equals("partly-cloudy-day")) {
//            iconId = R.drawable.cloudy;
//        }
//        else if (iconString.equals("partly-cloudy-night")) {
//            iconId = R.drawable.cloudy;
//        }
        return iconId;
    }

    public static String summaryLocalizer(String iconString) {
        String summary = "";

        if (iconString.equals("clear-day")) {
            summary = "Ясно";
        }
        else if (iconString.equals("clear-night")) {
            summary = "Ясно";
        }
        else if (iconString.equals("rain")) {
            summary = "Дождливо";
        }
        else if (iconString.equals("snow")) {
            summary = "Снежно";
        }
        else if (iconString.equals("sleet")) {
            summary = "Мокроснежно";
        }
        else if (iconString.equals("wind")) {
            summary = "Ветренно";
        }
        else if (iconString.equals("fog")) {
            summary = "Тумано";
        }
        else {
            summary = "Облачно";
        }
//        else if (iconString.equals("partly-cloudy-day")) {
//            summary = "Облачно";
//        }
//        else if (iconString.equals("partly-cloudy-night")) {
//            summary = "Облачно";
//        }
        return summary;

    }
}
