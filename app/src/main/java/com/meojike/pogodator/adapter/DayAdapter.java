package com.meojike.pogodator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meojike.pogodator.R;
import com.meojike.pogodator.weather.DailyWeather;

public class DayAdapter extends BaseAdapter {
    private Context mContext;
    private DailyWeather[] mDays;

    public DayAdapter(Context context, DailyWeather[] dailyWeathers) {
        mContext = context;
        mDays = dailyWeathers;
    }

    @Override
    public int getCount() {
        return mDays.length;
    }

    @Override
    public DailyWeather getItem(int i) {
        return mDays[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if(view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.daily_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.iconImageView = view.findViewById(R.id.iconImageView);
            viewHolder.temperatureLabel = view.findViewById(R.id.temperatureLabel);
            viewHolder.dayLabel = view.findViewById(R.id.dayName);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        DailyWeather dailyWeather = mDays[i];
        if(i == 0) {
            viewHolder.dayLabel.setText(dailyWeather.getDay().replaceFirst("^([\\w\\-]+)", "СЕГОДНЯ"));
        } else {
            viewHolder.dayLabel.setText(dailyWeather.getDay());
        }
        viewHolder.iconImageView.setImageResource(dailyWeather.getIconId());
        viewHolder.temperatureLabel.setText(dailyWeather.getTemperatureMax() + "°C");

        return view;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView temperatureLabel;
        TextView dayLabel;
    }
}
