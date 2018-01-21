package com.meojike.pogodator.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.meojike.pogodator.R;
import com.meojike.pogodator.adapter.DayAdapter;
import com.meojike.pogodator.weather.DailyWeather;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailyForecastActivity extends ListActivity {

    private DailyWeather[] mDailyWeathers;

    @BindView(R.id.locationString) TextView mLocationString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mLocationString.setText(intent.getStringExtra("currentCityName"));
        Parcelable[] arrayOfParceables = intent.getParcelableArrayExtra(MainPogodatorActivity.DAILY_FORECAST);
        mDailyWeathers = Arrays.copyOf(arrayOfParceables, arrayOfParceables.length, DailyWeather[].class);
        DayAdapter dayAdapter = new DayAdapter(this, mDailyWeathers);
        setListAdapter(dayAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String message = "";
        if(position != 0) {
//            if(dayOfTheWeek.toLowerCase().equals("среда") || dayOfTheWeek.toLowerCase().equals("пятница")
//                    || dayOfTheWeek.toLowerCase().equals("суббота")) {
//                pre = pre.replace();
//            } else {
            String dayOfTheWeek = mDailyWeathers[position].getDay();
            String pre = dayOfTheWeek.equals("Вторник") ? "Во вторник" : "В " + dayOfTheWeek.toLowerCase();
            message = String.format("%s максимальная температура будет равна %s°C и в целом будет %s ",
                    !pre.substring(pre.length() - 2, pre.length() - 1).equals("а") ? pre : pre.replace( (pre.substring(pre.length() - 2, pre.length() - 1)), "у"),
                    mDailyWeathers[position].getTemperatureMax(), mDailyWeathers[position].getSummary().toLowerCase());
        } else {
            message = String.format("Максимальная температура сегодня равна %s°C и в целом %s",
                    mDailyWeathers[0].getTemperatureMax(), mDailyWeathers[0].getSummary().toLowerCase());
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


}
