package com.meojike.pogodator.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.meojike.pogodator.R;
import com.meojike.pogodator.adapter.CityAdapter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CityChoiceActivity extends ListActivity {

    @BindView(R.id.cityName) EditText mCityName;

    private Geocoder mGeocoder;
    private List<android.location.Address> addressesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_choice);
        ButterKnife.bind(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();

        mGeocoder = new Geocoder(this, new Locale("ru", "RU"));
        mCityName.setHint(intent.getStringExtra("currentCityName").toUpperCase());
    }

    @OnClick (R.id.buttonFindCity)
    public void findCityByName(View view) {
        try {
            addressesArray = mGeocoder.getFromLocationName(mCityName.getText().toString(), 9);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < addressesArray.size(); i++) {
            if((addressesArray.get(i).getLocality() + "").equals("null")) {
                addressesArray.remove(i);
                i--;
            }

        }
        String[] citiesArray = new String[addressesArray.size()];

        for(int i = 0; i < addressesArray.size(); i++) {
            citiesArray[i] = addressesArray.get(i).getLocality() + ",\n\t " + addressesArray.get(i).getCountryName();
        }

        CityAdapter citiesAdapter = new CityAdapter(this, citiesArray);
        setListAdapter(citiesAdapter);

        if(citiesArray.length == 0) {
            Toast.makeText(this, "Такого города не найдено :(", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Нажмите на название города, чтобы выбрать его", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        double latitude = addressesArray.get(position).getLatitude();
        double longitude = addressesArray.get(position).getLongitude();
        Intent intent = new Intent(this, MainPogodatorActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("makeNewRequest", true);
        intent.putExtra("cityName", addressesArray.get(position).getLocality());
        Log.d(CityChoiceActivity.class.getSimpleName(), "lat and long: " + latitude + " " + longitude);
        startActivity(intent);
    }
}
