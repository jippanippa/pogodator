package com.meojike.pogodator.adapter;

import android.content.Context;
//import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meojike.pogodator.R;
import com.meojike.pogodator.weather.HourlyWeather;

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourlyViewHolder> {

    private HourlyWeather[] mHourlyWeathers;
    private Context mContext;

    public HourAdapter(Context context, HourlyWeather[] arrayOfHourltWeathers) {
        mHourlyWeathers = arrayOfHourltWeathers;
        mContext = context;
    }

    @Override
    public HourlyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_item_layout, parent, false);
        HourlyViewHolder viewHolder = new HourlyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HourlyViewHolder holder, int position) {

        holder.bindHour(mHourlyWeathers[position]);
    }

    @Override
    public int getItemCount() {
        return mHourlyWeathers.length;
    }

    public class HourlyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mWeatherIcon;
        public TextView mTimeLabel;
        public TextView mSummaryLabel;
        public TextView mTemperatureLabel;

        public HourlyViewHolder(View itemView) {
            super(itemView);

            mWeatherIcon = itemView.findViewById(R.id.iconImageView);
            mTimeLabel = itemView.findViewById(R.id.timeLabel);
            mSummaryLabel = itemView.findViewById(R.id.summaryLabel);
            mTemperatureLabel = itemView.findViewById(R.id.hourlyTemperatureLabel);

            itemView.setOnClickListener(this);
        }

        public void bindHour(HourlyWeather hourlyWeather) {
            mWeatherIcon.setImageResource(hourlyWeather.getIconId());
            mTimeLabel.setText(hourlyWeather.getHour());
            mSummaryLabel.setText(hourlyWeather.getSummary());
            mTemperatureLabel.setText(hourlyWeather.getTemperature() + "°C");
        }

        @Override
        public void onClick(View view) {
            String message = "В " + mTimeLabel.getText().toString() + " будет " + mTemperatureLabel.getText()
                    + " и " + mSummaryLabel.getText().toString().toLowerCase();
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }
}
