package com.geekbrains.weather.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geekbrains.weather.GetCurrentIndex;
import com.geekbrains.weather.R;

import static com.geekbrains.weather.fragments.CitiesFragments.keyForAirHumidity;
import static com.geekbrains.weather.fragments.CitiesFragments.keyForIndex;
import static com.geekbrains.weather.fragments.CitiesFragments.keyForPressure;
import static com.geekbrains.weather.fragments.CitiesFragments.keyForWindSpeed;

public class WeatherFragment extends Fragment {

    private TextView cityName;
    private ImageView weatherStatus;
    private TextView temperature;
    private TextView airHumidity;
    private TextView windSpeed;
    private TextView weatherPressure;
    private RelativeLayout airHumidityLayout;
    private RelativeLayout windSpeedLayout;
    private RelativeLayout pressureLayout;
    private GetCurrentIndex currentIndex;


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
        initViews(view);
        setCityName();
        setImgWeatherStatus();
        setWeatherTemp();
        setAirHumidity();
        setWindSpeed();
        setPressure();
        setAdditionalWeatherData();
        currentIndex.getCurrentIndex(getIndex());
    }

    private void initViews(View view) {
        cityName = view.findViewById(R.id.cityName);
        weatherStatus = view.findViewById(R.id.weatherStatus);
        temperature = view.findViewById(R.id.weatherDegree);
        airHumidityLayout = view.findViewById(R.id.air_humidity);
        windSpeedLayout = view.findViewById(R.id.wind_speed);
        pressureLayout = view.findViewById(R.id.pressure);
        airHumidity = view.findViewById(R.id.air_humidity_data);
        windSpeed = view.findViewById(R.id.wind_speed_data);
        weatherPressure = view.findViewById(R.id.pressure_data);
    }

    private void setCityName() {
        String[] cityNames = getResources().getStringArray(R.array.cities);
        cityName.setText(cityNames[getIndex()]);
    }

    private void setImgWeatherStatus() {
        @SuppressLint("Recycle") TypedArray imgs = getResources().obtainTypedArray(R.array.weather_imgs);
        weatherStatus.setImageResource(imgs.getResourceId(getIndex(), -1));
    }

    private void setWeatherTemp() {
        String[] temp = getResources().getStringArray(R.array.temp);
        temperature.setText(temp[getIndex()]);
    }

    private void setAirHumidity() {
        String[] humidity = getResources().getStringArray(R.array.air_humidity);
        airHumidity.setText(humidity[getIndex()]);
    }

    private void setWindSpeed() {
        String[] wind = getResources().getStringArray(R.array.wind_speed);
        windSpeed.setText(wind[getIndex()]);
    }

    private void setPressure() {
        String[] pressure = getResources().getStringArray(R.array.pressure);
        weatherPressure.setText(pressure[getIndex()]);
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
}

