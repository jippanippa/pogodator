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

        int iconId;

        if (iconString.equals("clear-day") || iconString.equals("sunny")) {
            iconId = R.drawable.sunny;
        }
        else if (iconString.equals("clear-night")) {
            iconId = R.drawable.clear_day;
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

        return iconId;
    }

}
