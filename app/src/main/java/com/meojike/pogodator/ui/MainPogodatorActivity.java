package com.meojike.pogodator.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meojike.pogodator.R;
import com.meojike.pogodator.weather.CurrentWeather;
import com.meojike.pogodator.weather.DailyWeather;
import com.meojike.pogodator.weather.Forecast;
import com.meojike.pogodator.weather.HourlyWeather;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainPogodatorActivity extends AppCompatActivity {

    public static final String TAG = MainPogodatorActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";
    final int REQUEST_CODE = 123;
    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    private Forecast mForecast;
    private LocationManager mLocationManager;
    private Location mLocation;
    private LocationListener mLocationListener;


    private double latitude;
    private double longitude;
    private boolean initialStart;
    private Geocoder mGeocoder;

    @BindView(R.id.locationLabel) TextView mCurrentCityNameLabel;
    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R.id.iconImageView) ImageView mIconImageView;
    @BindView(R.id.refreshImageView) ImageView mRefreshImageView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.changeCityButton) Button mChangeCityButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pogodator);
        ButterKnife.bind(this);
        mGeocoder = new Geocoder(this, new Locale("ru", "RU"));
        getCurrentLocationCoordinates();
        initialStart = true;

        mProgressBar.setVisibility(View.INVISIBLE);
        mRefreshImageView.setOnClickListener(view -> getForecast(latitude, longitude));

        Log.d(TAG, "OnCreate was called. Main UI code is running!");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "OnResume was called");
        super.onResume();
        Intent intent = getIntent();

        if(initialStart) {
            Log.d(TAG, "INITIAL START was called");
            getForecast(latitude, longitude);
            initialStart = false;
        }

        if(intent.getBooleanExtra("makeNewRequest", false)) {
            latitude = intent.getDoubleExtra("latitude", latitude);
            longitude = intent.getDoubleExtra("longitude", longitude);
            initialStart = intent.getBooleanExtra("makeNewRequest", false);
            setCurrentCityNameLabel(intent.getStringExtra("cityName"));
            getForecast(latitude, longitude);
            Log.d(TAG, "notfirstrequest was called " + latitude + " " + longitude);
        }

    }

    private void getCurrentLocationCoordinates() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged() callback received");
//                longitude = Double.valueOf(location.getLongitude());
//                latitude = Double.valueOf(location.getLatitude());
//                try {
//                    if(mCurrentCityNameLabel.getText().toString()
//                            .equals(mGeocoder.getFromLocation(latitude, longitude, 1).get(0).getLocality())) {
//                        getForecast(latitude, longitude);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                Log.d(TAG, "Callback latitude and longitude: " + latitude + " " + longitude);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d(TAG, "onStatusChanged() callback received");
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d(TAG, "onProviderEnabled() callback received");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d(TAG, "onProviderDisabled() callback received");
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        latitude = mLocation.getLatitude();
        longitude = mLocation.getLongitude();
        Log.d(TAG, "Non-callback latitude and longitude: " + latitude + " " + longitude);

        setLocationLabel(latitude, longitude, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "onRequestPermissionsResult() permission GRANTED!");
                getCurrentLocationCoordinates();
            } else {
                Log.d("TAG", "Permission denied :(");
                Intent intent = new Intent(this, CityChoiceActivity.class);
                intent.putExtra("currentCityName", "введите имя города");
                startActivity(intent);
            }
        }
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();
        forecast.setCurrentWeather(getCurrentDetails(jsonData));
        forecast.setHourlyWeatherArray(getHourlyForecast(jsonData));
        forecast.setDailyWeatherArray(getDailyForecast(jsonData));
        return forecast;
    }

    private DailyWeather[] getDailyForecast(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");
        DailyWeather[] dailyWeathers = new DailyWeather[data.length()];
        for(int i = 0; i < dailyWeathers.length; i++) {
            DailyWeather dailyWeather = new DailyWeather();
            dailyWeather.setIcon(data.getJSONObject(i).getString("icon"));
            dailyWeather.setSummary(data.getJSONObject(i).getString("summary"));
            dailyWeather.setTemperatureMax(data.getJSONObject(i).getDouble("temperatureMax"));
            dailyWeather.setTime(data.getJSONObject(i).getLong("time"));
            dailyWeather.setTimezone(timezone);
            dailyWeathers[i] = dailyWeather;
        }
        return dailyWeathers;
    }

    private HourlyWeather[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");
        HourlyWeather[] hourlyWeathers = new HourlyWeather[data.length()];
        for(int i = 0; i < data.length(); i++) {
            HourlyWeather hourlyWeather = new HourlyWeather();
            hourlyWeather.setTimezone(timezone);
            hourlyWeather.setSummary(data.getJSONObject(i).getString("summary"));
            hourlyWeather.setTime(data.getJSONObject(i).getLong("time"));
            hourlyWeather.setTemperature(data.getJSONObject(i).getDouble("temperature"));
            hourlyWeather.setIcon(data.getJSONObject(i).getString("icon"));
            hourlyWeathers[i] = hourlyWeather;
        }
        return hourlyWeathers;
    }

    private void getForecast(double latitude, double longitude) {
        String apiKey = "611e9019f0c696e47d5c0e05b23f231e";
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey +
                "/" + latitude + "," + longitude + "?lang=ru ";

        if (isNetworkAvailable()) {
            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(() -> toggleRefresh());

                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(() -> toggleRefresh());
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast = parseForecastDetails(jsonData);
                            runOnUiThread(() -> updateDisplay());
                        } else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                    catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        }
        else {
            Toast.makeText(this, getString(R.string.network_unavailable_message),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        CurrentWeather currentWeather = mForecast.getCurrentWeather();
        mTemperatureLabel.setText(currentWeather.getTemperature() + "");
        mTimeLabel.setText("В " + currentWeather.getFormattedTime() + " ТАКАЯ ПОГОДА");
        mHumidityValue.setText(currentWeather.getHumidity() + "");
        mPrecipValue.setText(currentWeather.getPrecipChance() + "%");
        mSummaryLabel.setText(currentWeather.getSummary().toUpperCase());

        Drawable drawable = getResources().getDrawable(mForecast.getCurrentWeather().getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);

        Log.d(TAG, currentWeather.getFormattedTime());

        return currentWeather;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertFragment dialog = new AlertFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    private void setLocationLabel(double latitude, double longitude, int count) {
        try {
            List<Address> cityStrings =  mGeocoder.getFromLocation(latitude, longitude, count);
            mCurrentCityNameLabel.setText(cityStrings.get(0).getLocality().toUpperCase());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCurrentCityNameLabel(String cityName) {
        mCurrentCityNameLabel.setText(cityName.toUpperCase());
    }

    @OnClick (R.id.dailyButton)
    public void startDailyActivity(View view) {
        Intent intent = new Intent(this, DailyForecastActivity.class);
        intent.putExtra(DAILY_FORECAST, mForecast.getDailyWeatherArray());
        intent.putExtra("currentCityName", mCurrentCityNameLabel.getText());

        startActivity(intent);

    }


    @OnClick (R.id.hourlyButton)
    public void startHourlyActivity(View view) {
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        intent.putExtra(HOURLY_FORECAST, mForecast.getHourlyWeatherArray());
        intent.putExtra("currentCityName", mCurrentCityNameLabel.getText());

        startActivity(intent);
    }

    @OnClick (R.id.changeCityButton)
    public void startCityChoiceActivity(View view) {
        Intent intent = new Intent(this, CityChoiceActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("currentCityName", mCurrentCityNameLabel.getText());

        startActivity(intent);
    }

}
