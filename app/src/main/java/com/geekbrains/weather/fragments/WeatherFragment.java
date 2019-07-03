package com.geekbrains.weather.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geekbrains.weather.database.MyDatabaseHelper;
import com.geekbrains.weather.database.WeatherTable;
import com.geekbrains.weather.support.GetOptionsData;
import com.geekbrains.weather.R;
import com.geekbrains.weather.support.OpenWeatherRepo;
import com.geekbrains.weather.support.entites.WeatherRequestRestModel;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.geekbrains.weather.fragments.CitiesFragments.keyForIndex;

public class WeatherFragment extends Fragment {

    public static final String keyForAirHumidity = "keyForAirHumidity";
    public static final String keyForWindSpeed = "keyForWindSpeed";
    public static final String keyForPressure = "keyForPressure";

    private TextView cityName;
    private TextView temperature;
    private TextView airHumidity;
    private TextView windSpeed;
    private TextView weatherPressure;
    private boolean airHumidityFlag;
    private boolean windSpeedFlag;
    private boolean pressureFlag;
    private RelativeLayout airHumidityLayout;
    private RelativeLayout windSpeedLayout;
    private RelativeLayout pressureLayout;
    private GetOptionsData currentOptionsData;

    WeatherRequestRestModel model = new WeatherRequestRestModel();

    SQLiteDatabase database;

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
        currentOptionsData = (GetOptionsData) context;
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
        initDB();
        initViews(view);
        getFlags();
        setAdditionalWeatherData();
        requestRetrofit(getCity());
        currentOptionsData.getCurrentIndex(getIndex(),
                airHumidityFlag,
                windSpeedFlag,
                pressureFlag);
        showCitiesList();
    }

    private void showCitiesList() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) {
                Fragment fragment = fragmentManager.findFragmentById(R.id.main_container_two);
                if (fragment != null) {
                    fragment = new CitiesFragments();
                    Bundle args = new Bundle();
                    args.putBoolean(keyForAirHumidity, getHumidity());
                    args.putBoolean(keyForWindSpeed, getWindSpeed());
                    args.putBoolean(keyForPressure, getPressure());
                    fragment.setArguments(args);
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_container_two, fragment)
                            .commit();
                } else {
                    fragment = new CitiesFragments();
                    Bundle args = new Bundle();
                    args.putBoolean(keyForAirHumidity, getHumidity());
                    args.putBoolean(keyForWindSpeed, getWindSpeed());
                    args.putBoolean(keyForPressure, getPressure());
                    fragment.setArguments(args);
                    fragmentManager.beginTransaction()
                            .add(R.id.main_container_two, fragment)
                            .commit();
                }
            }
        }
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

    private void initDB() {
        database = new MyDatabaseHelper(Objects.requireNonNull(getActivity()).getApplicationContext()).getWritableDatabase();
    }

    private String getCity() {
        String city;
        String[] cityNames = getResources().getStringArray(R.array.cities_eng);
        city = cityNames[getIndex()];
        return city;
    }

    private void setAdditionalWeatherData() {
        if (airHumidityFlag) {
            airHumidityLayout.setVisibility(View.VISIBLE);
        } else {
            airHumidityLayout.setVisibility(View.GONE);
        }
        if (windSpeedFlag) {
            windSpeedLayout.setVisibility(View.VISIBLE);
        } else {
            windSpeedLayout.setVisibility(View.GONE);
        }
        if (pressureFlag) {
            pressureLayout.setVisibility(View.VISIBLE);
        } else {
            pressureLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            int id = item.getItemId();
            switch (id) {
                case R.id.menu_air_humidity_check:
                    item.setChecked(!item.isChecked());
                    airHumidityFlag = item.isChecked();
                    showWeather();
                    break;
                case R.id.menu_wind_speed_check:
                    item.setChecked(!item.isChecked());
                    windSpeedFlag = item.isChecked();
                    showWeather();
                    break;
                case R.id.menu_pressure_check:
                    item.setChecked(!item.isChecked());
                    pressureFlag = item.isChecked();
                    showWeather();
                    break;
                case R.id.menu_list_of_cities:
                    FragmentManager fragmentManager = getFragmentManager();
                    if (fragmentManager != null) {
                        Fragment fragment = fragmentManager.findFragmentById(R.id.main_container);
                        if (fragment != null) {
                            fragment = new CitiesFragments();
                            Bundle args = new Bundle();
                            args.putBoolean(keyForAirHumidity, getHumidity());
                            args.putBoolean(keyForWindSpeed, getWindSpeed());
                            args.putBoolean(keyForPressure, getPressure());
                            fragment.setArguments(args);
                            fragmentManager.beginTransaction()
                                    .replace(R.id.main_container, fragment)
                                    .commit();
                        }
                    }
                    break;
                default:
                    return false;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showWeather() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            Fragment fragment = fragmentManager.findFragmentById(R.id.main_container);
            if (fragment != null) {
                fragment = WeatherFragment.create(getIndex(), airHumidityFlag, windSpeedFlag, pressureFlag);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_container, fragment)
                        .commit();
            }
        }
    }

    private void setCityName(String city) {
        String[] cityNames = getResources().getStringArray(R.array.cities);
        String cityText = cityNames[getIndex()] + ", "
                + WeatherTable.getCountry(city, database);
        cityName.setText(cityText);
    }

    private void setWeatherTemp(String city) {
        @SuppressLint("DefaultLocale")
        String currentTemp = String.format("%.2f", WeatherTable.getTemp(city, database)) + "\u2103";
        temperature.setText(currentTemp);
    }

    private void setAdditionalOptions(String city) {
        String humidity = WeatherTable.getHumidity(city, database) + "%";
        @SuppressLint("DefaultLocale")
        String windV = String.format("%.2f", WeatherTable.getWindSpeed(city, database)) + "m/s";
        String pressure = WeatherTable.getPressure(city, database) + "hPa";
        airHumidity.setText(humidity);
        windSpeed.setText(windV);
        weatherPressure.setText(pressure);
    }

    private void requestRetrofit(final String city) {
        OpenWeatherRepo.getSingleton().getAPI().loadWeather(city + ",ru",
                "8520ef99989f81f6e1af12c2fb84e086", "metric")
                .enqueue(new Callback<WeatherRequestRestModel>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherRequestRestModel> call,
                                           @NonNull Response<WeatherRequestRestModel> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            model = response.body();
                            setDataBase(city);
                            setCityName(city);
                            setWeatherTemp(city);
                            setAdditionalOptions(city);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherRequestRestModel> call, @NonNull Throwable t) {
                        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.failed_to_update),
                                Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void setDataBase(String city) {
        float temp = model.main.temp;
        int humidity = model.main.humidity;
        float windV = model.wind.speed;
        int pressure = model.main.pressure;
        String country = model.sys.country;
        if (!WeatherTable.ifExists(city, database)) {
            WeatherTable.add(city, temp, humidity, windV, pressure, country, database);
        } else {
            WeatherTable.edit(city, temp, humidity, windV, pressure, country, database);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (this.isVisible()) {
            MenuItem options = menu.findItem(R.id.options);
            if (options != null) {
                options.setVisible(true);
            }
            MenuItem listCities = menu.findItem(R.id.menu_list_of_cities);
            if (listCities != null) {
                listCities.setVisible(true);
            }
            MenuItem history = menu.findItem(R.id.menu_history);
            if (history != null) {
                history.setVisible(true);
            }
            MenuItem myCity = menu.findItem(R.id.menu_my_city);
            if (myCity != null) {
                myCity.setVisible(true);
            }
            MenuItem sensors = menu.findItem(R.id.menu_sensors);
            if (sensors != null) {
                sensors.setVisible(true);
            }
        }
    }

    private void getFlags () {
        if (getArguments() != null) {
            airHumidityFlag = getArguments().getBoolean(keyForAirHumidity, false);
            windSpeedFlag = getArguments().getBoolean(keyForWindSpeed, false);
            pressureFlag = getArguments().getBoolean(keyForPressure, false);
        }
    }
}

