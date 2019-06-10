package com.geekbrains.weather.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geekbrains.weather.support.GetCurrentIndex;
import com.geekbrains.weather.R;
import com.geekbrains.weather.support.OpenWeatherRepo;
import com.geekbrains.weather.support.entites.WeatherRequestRestModel;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.geekbrains.weather.fragments.CitiesFragments.keyForAirHumidity;
import static com.geekbrains.weather.fragments.CitiesFragments.keyForIndex;
import static com.geekbrains.weather.fragments.CitiesFragments.keyForPressure;
import static com.geekbrains.weather.fragments.CitiesFragments.keyForWindSpeed;

public class WeatherFragment extends Fragment {

    private TextView cityName;
    private TextView temperature;
    private TextView airHumidity;
    private TextView windSpeed;
    private TextView weatherPressure;
    private RelativeLayout airHumidityLayout;
    private RelativeLayout windSpeedLayout;
    private RelativeLayout pressureLayout;
    private GetCurrentIndex currentIndex;

    WeatherRequestRestModel model = new WeatherRequestRestModel();

    public static WeatherFragment create(int index, boolean humidity, boolean wind, boolean pressure) {
        WeatherFragment f = new WeatherFragment();

        Bundle args = new Bundle();
        args.putInt(keyForIndex, index);
        args.putBoolean(keyForAirHumidity, humidity);
        args.putBoolean(keyForWindSpeed, wind);
        args.putBoolean(keyForPressure, pressure);
        f.setArguments(args);
        return f;
    }

    public int getIndex() {
        int index = 0;
        if (getArguments() != null) {
            index = getArguments().getInt(keyForIndex, 0);
        }
        return index;
    }

    private boolean getHumidity() {
        boolean humidity = false;
        if (getArguments() != null) {
            humidity = getArguments().getBoolean(keyForAirHumidity, false);
        }
        return humidity;
    }

    private boolean getWindSpeed() {
        boolean windSpeed = false;
        if (getArguments() != null) {
            windSpeed = getArguments().getBoolean(keyForWindSpeed, false);
        }
        return windSpeed;
    }

    private boolean getPressure() {
        boolean pressure = false;
        if (getArguments() != null) {
            pressure = getArguments().getBoolean(keyForPressure, false);
        }
        return pressure;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        currentIndex = (GetCurrentIndex) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.weather_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        initViews(view);
        setAdditionalWeatherData();
        requestRetrofit(getCity());
        currentIndex.getCurrentIndex(getIndex());
    }

    private void initViews(View view) {
        cityName = view.findViewById(R.id.cityName);
        temperature = view.findViewById(R.id.weatherDegree);
        airHumidityLayout = view.findViewById(R.id.air_humidity);
        windSpeedLayout = view.findViewById(R.id.wind_speed);
        pressureLayout = view.findViewById(R.id.pressure);
        airHumidity = view.findViewById(R.id.temperature_data);
        windSpeed = view.findViewById(R.id.humidity_data);
        weatherPressure = view.findViewById(R.id.pressure_data);
    }

    private String getCity() {
        String city;
        String[] cityNames = getResources().getStringArray(R.array.cities_eng);
        city = cityNames[getIndex()];
        return city;
    }

    private void setAdditionalWeatherData() {
        if (getHumidity()) {
            airHumidityLayout.setVisibility(View.VISIBLE);
        } else {
            airHumidityLayout.setVisibility(View.GONE);
        }
        if (getWindSpeed()) {
            windSpeedLayout.setVisibility(View.VISIBLE);
        } else {
            windSpeedLayout.setVisibility(View.GONE);
        }
        if (getPressure()) {
            pressureLayout.setVisibility(View.VISIBLE);
        } else {
            pressureLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_my_city) {
            final SharedPreferences defaultPrefs =
                    PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()).getApplicationContext());
            saveToPreference(defaultPrefs);
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.my_city_message),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveToPreference(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        int cityIndex = getIndex();
        editor.putInt(keyForIndex, cityIndex);
        editor.apply();
    }

    private void setCityName() {
        String[] cityNames = getResources().getStringArray(R.array.cities);
        String city = cityNames[getIndex()] + ", "
                + model.sys.country;
        cityName.setText(city);
    }

    private void setWeatherTemp() {
        @SuppressLint("DefaultLocale")
        String currentTemp = String.format("%.2f", model.main.temp) + "\u2103";
        temperature.setText(currentTemp);
    }

    private void setAdditionalOptions() {
        String humidity = model.main.humidity + "%";
        String windV = model.wind.speed + "m/s";
        String pressure = model.main.pressure + "hPa";
        airHumidity.setText(humidity);
        windSpeed.setText(windV);
        weatherPressure.setText(pressure);
    }

    private void requestRetrofit(String city) {
        OpenWeatherRepo.getSingleton().getAPI().loadWeather(city + ",ru",
                "8520ef99989f81f6e1af12c2fb84e086", "metric")
                .enqueue(new Callback<WeatherRequestRestModel>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherRequestRestModel> call,
                                           @NonNull Response<WeatherRequestRestModel> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            model = response.body();
                            setCityName();
                            setWeatherTemp();
                            setAdditionalOptions();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherRequestRestModel> call, @NonNull Throwable t) {
                        temperature.setText(R.string.error);
                        airHumidity.setText(R.string.error);
                        windSpeed.setText(R.string.error);
                        weatherPressure.setText(R.string.error);
                    }
                });

    }
}

