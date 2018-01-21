package com.meojike.pogodator.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.meojike.pogodator.R;
import com.meojike.pogodator.adapter.HourAdapter;
import com.meojike.pogodator.weather.HourlyWeather;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HourlyForecastActivity extends AppCompatActivity {

    HourlyWeather[] mHourlyWeathers;
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Parcelable[] arrayOfParceables = intent.getParcelableArrayExtra(MainPogodatorActivity.HOURLY_FORECAST);
        mHourlyWeathers = Arrays.copyOf(arrayOfParceables, arrayOfParceables.length, HourlyWeather[].class);

        HourAdapter adapter = new HourAdapter(this, mHourlyWeathers);
        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
    }



}
