package com.meojike.pogodator.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meojike.pogodator.R;

public class CityAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mCities;

    public CityAdapter(Context context, String[] cities) {
        mContext = context;
        mCities = cities;
    }

    @Override
    public int getCount() {
        return mCities.length;
    }

    @Override
    public Object getItem(int i) {
        return mCities[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if(view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.city_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.cityName = view.findViewById(R.id.itemCityName);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        String cityName = mCities[i];

        viewHolder.cityName.setText(cityName);

        return view;
    }

    private static class ViewHolder {
        TextView cityName;
    }
}
