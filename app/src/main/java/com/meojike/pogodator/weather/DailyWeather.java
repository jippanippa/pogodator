package com.meojike.pogodator.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DailyWeather implements Parcelable {
    private long mTime;
    private String mSummary;
    private double mTemperatureMax;
    private String mIcon;
    private String mTimezone;

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public int getTemperatureMax() {
        return (int) Math.round(mTemperatureMax);
    }

    public void setTemperatureMax(double temperatureMax) {
        mTemperatureMax = (temperatureMax - 32) * 5/9;
    }

    public String getIcon() {
        return mIcon;
    }

    public int getIconId() {
        return Forecast.getIconId(mIcon);
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }



    public String getDay(){
        String dayInRussian = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(mTimezone));
        Date dateTime = new Date(mTime * 1000);
        if(simpleDateFormat.format(dateTime).toLowerCase().equals("monday")) {
            dayInRussian = "Понедельник";
        } else if(simpleDateFormat.format(dateTime).toLowerCase().equals("tuesday")) {
            dayInRussian = "Вторник";
        } else if(simpleDateFormat.format(dateTime).toLowerCase().equals("wednesday")) {
            dayInRussian = "Среда";
        } else if(simpleDateFormat.format(dateTime).toLowerCase().equals("thursday")) {
            dayInRussian = "Четверг";
        } else if(simpleDateFormat.format(dateTime).toLowerCase().equals("friday")) {
            dayInRussian = "Пятница";
        } else if(simpleDateFormat.format(dateTime).toLowerCase().equals("saturday")) {
            dayInRussian = "Суббота";
        } else {
            dayInRussian = "Воскресенье";
        }
        return dayInRussian;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
            parcel.writeLong(mTime);
            parcel.writeString(mSummary);
            parcel.writeDouble(mTemperatureMax);
            parcel.writeString(mIcon);
            parcel.writeString(mTimezone);
    }

    public DailyWeather(){ }

    private DailyWeather(Parcel incoming) {
        mTime = incoming.readLong();
        mSummary = incoming.readString();
        mTemperatureMax = incoming.readDouble();
        mIcon = incoming.readString();
        mTimezone = incoming.readString();
    }

    public static final Creator<DailyWeather> CREATOR = new Creator<DailyWeather>() {
        @Override
        public DailyWeather createFromParcel(Parcel parcel) {
            return new DailyWeather(parcel);
        }

        @Override
        public DailyWeather[] newArray(int i) {
            return new DailyWeather[i];
        }
    };
}
