package com.meojike.pogodator.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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


public class MainPogodatorActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public static final String TAG = MainPogodatorActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";
    final int REQUEST_CODE = 123;

    private Forecast mForecast;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    
    public static double latitude;
    public static double longitude;
    private boolean mustNotGetLocalCoordinates;
    private int askCounter;
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
    @BindView(R.id.hourlyButton) Button mHourlyButton;
    @BindView(R.id.dailyButton) Button mDailyButton;
    @BindView(R.id.degreeImageView) ImageView mDegreeImageView;
    @BindView(R.id.humidityLabel) TextView mHumidityLabel;
    @BindView(R.id.precipLabel) TextView mPrecipLabel;
    @BindView(R.id.waitText) TextView mWaitText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pogodator);
        ButterKnife.bind(this);
        mGeocoder = new Geocoder(this, new Locale("ru", "RU"));

        mProgressBar.setVisibility(View.INVISIBLE);
        mRefreshImageView.setOnClickListener(view -> getForecast(latitude, longitude));
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Log.d(TAG, "OnCreate was called. Main UI code is running!");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "OnResume was called");
        super.onResume();

        Intent intent = getIntent();
        if(intent.getBooleanExtra("makeNewRequest", false)) {
            latitude = intent.getDoubleExtra("latitude", latitude);
            longitude = intent.getDoubleExtra("longitude", longitude);
            setCurrentCityNameLabel(intent.getStringExtra("cityName"));
            getForecast(latitude, longitude);
            Log.d(TAG, "notfirstrequest was called " + latitude + " " + longitude);
        } else {
            Log.d(TAG, "INITIAL START was called ");
            if(!mustNotGetLocalCoordinates) getCurrentLocationCoordinates();
        }

    }

    private void getCurrentLocationCoordinates() {
        Log.d(TAG, "getCurrentLocationCoordinates() was called");
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> {

            if (ActivityCompat.checkSelfPermission(MainPogodatorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainPogodatorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainPogodatorActivity.this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                return;
            }

            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(MainPogodatorActivity.this, location -> {
                        if (location != null) {
                            Log.d(TAG, "location is not null");
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            getForecast(latitude, longitude);
                            setLocationLabel(latitude, longitude, 1);
                        } else {
                            Log.d(TAG, "location is null");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                                Intent i = getBaseContext().getPackageManager()
                                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                        }
                    });
        });

            task.addOnFailureListener(this, e -> {
                Log.d(TAG, "Time to ask to turn on location");
                if (e instanceof ResolvableApiException && askCounter < 1) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainPogodatorActivity.this, 0x1);
                        askCounter++;
                    } catch (IntentSender.SendIntentException sendEx) {
                    }
                } else {
                    goToManualCityChoice();
                }
            });
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
                mustNotGetLocalCoordinates = true;
                goToManualCityChoice();
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
            turnAnimationOn();
        } else {
            turnAnimationOff();
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

    private void goToManualCityChoice() {
        Intent intent = new Intent(this, CityChoiceActivity.class);
        intent.putExtra("currentCityName", "введите имя города");
        startActivity(intent);
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void turnAnimationOn() {
        mCurrentCityNameLabel.setVisibility(View.INVISIBLE);
        mTimeLabel.setVisibility(View.INVISIBLE);
        mTemperatureLabel.setVisibility(View.INVISIBLE);
        mHumidityValue.setVisibility(View.INVISIBLE);
        mPrecipValue.setVisibility(View.INVISIBLE);
        mSummaryLabel.setVisibility(View.INVISIBLE);
        mIconImageView.setVisibility(View.INVISIBLE);
        mRefreshImageView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mChangeCityButton.setVisibility(View.INVISIBLE);
        mHourlyButton.setVisibility(View.INVISIBLE);
        mDailyButton.setVisibility(View.INVISIBLE);
        mDegreeImageView.setVisibility(View.INVISIBLE);
        mHumidityLabel.setVisibility(View.INVISIBLE);
        mPrecipLabel.setVisibility(View.INVISIBLE);
        mWaitText.setVisibility(View.VISIBLE);
    }

    public void turnAnimationOff() {
        mCurrentCityNameLabel.setVisibility(View.VISIBLE);
        mTimeLabel.setVisibility(View.VISIBLE);
        mTemperatureLabel.setVisibility(View.VISIBLE);
        mHumidityValue.setVisibility(View.VISIBLE);
        mPrecipValue.setVisibility(View.VISIBLE);
        mSummaryLabel.setVisibility(View.VISIBLE);
        mIconImageView.setVisibility(View.VISIBLE);
        mRefreshImageView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mChangeCityButton.setVisibility(View.VISIBLE);
        mHourlyButton.setVisibility(View.VISIBLE);
        mDailyButton.setVisibility(View.VISIBLE);
        mDegreeImageView.setVisibility(View.VISIBLE);
        mHumidityLabel.setVisibility(View.VISIBLE);
        mPrecipLabel.setVisibility(View.VISIBLE);
        mWaitText.setVisibility(View.INVISIBLE);
    }
}