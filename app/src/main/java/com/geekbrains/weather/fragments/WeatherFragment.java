package com.geekbrains.weather.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.geekbrains.weather.support.WeatherDataLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static com.geekbrains.weather.fragments.CitiesFragments.keyForAirHumidity;
import static com.geekbrains.weather.fragments.CitiesFragments.keyForIndex;
import static com.geekbrains.weather.fragments.CitiesFragments.keyForPressure;
import static com.geekbrains.weather.fragments.CitiesFragments.keyForWindSpeed;

public class WeatherFragment extends Fragment {

    private final Handler handler = new Handler();

    private TextView cityName;
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
        setHasOptionsMenu(true);
        initViews(view);
        setAdditionalWeatherData();
        updateWeatherData(getCity());
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

    private void updateWeatherData(final String city) {
        new Thread() {
            @Override
            public void run() {
                final JSONObject jsonObject = WeatherDataLoader.getJSONData(city);
                if(jsonObject == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), R.string.place_not_found, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            renderWeather(jsonObject);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject jsonObject) {
        try {
            JSONObject main = jsonObject.getJSONObject("main");
            JSONObject wind = jsonObject.getJSONObject("wind");

            setCityName(jsonObject);
            setWeatherTemp(main);
            setAdditionalOptions(main, wind);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void setCityName(JSONObject jsonObject) throws JSONException {
        String[] cityNames = getResources().getStringArray(R.array.cities);
        String city = cityNames[getIndex()] + ", "
                + jsonObject.getJSONObject("sys").getString("country");
        cityName.setText(city);
    }

    private void setWeatherTemp(JSONObject main) throws JSONException {
        @SuppressLint("DefaultLocale")
        String currentTemp = String.format("%.2f", main.getDouble("temp")) + "\u2103";
        temperature.setText(currentTemp);
    }

    private void setAdditionalOptions(JSONObject main, JSONObject wind) throws JSONException {
        String humidity = main.getString("humidity") + "%";
        String windV = wind.getString("speed") + "m/s";
        String pressure = main.getString("pressure") + "hPa";
        airHumidity.setText(humidity);
        windSpeed.setText(windV);
        weatherPressure.setText(pressure);
    }
}

