package com.geekbrains.weather.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.geekbrains.weather.R;
import com.geekbrains.weather.SecondActivity;
import com.geekbrains.weather.support.CitiesFragmentsAdapter;

public class CitiesFragments extends Fragment {

    public static final String keyForIndex = "index";
    public static final String keyForAirHumidity = "keyForAirHumidity";
    public static final String keyForWindSpeed = "keyForWindSpeed";
    public static final String keyForPressure = "keyForPressure";

    private ListView listCities;
    private int currentPosition = 0;

    private Bundle savedInstanceState = null;

    private boolean airHumidityFlag;
    private boolean windSpeedFlag;
    private boolean pressureFlag;

    private Menu menu;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        initViews(view);
        getFlags(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_air_humidity_check:
                item.setChecked(!item.isChecked());
                airHumidityFlag = item.isChecked();
                break;
            case R.id.menu_wind_speed_check:
                item.setChecked(!item.isChecked());
                windSpeedFlag = item.isChecked();
                break;
            case R.id.menu_pressure_check:
                item.setChecked(!item.isChecked());
                pressureFlag = item.isChecked();
                break;
            default:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void initViews(View view) {
        listCities = view.findViewById(R.id.cities_list_view);
    }

    private void init() {
        FragmentActivity fragmentActivity = getActivity();
        String[] cities = getResources().getStringArray(R.array.cities);

        if(fragmentActivity != null) {
            CitiesFragmentsAdapter adapter = new CitiesFragmentsAdapter(fragmentActivity,cities);
            listCities.setAdapter(adapter);
            setClickListener(listCities);

            if (savedInstanceState != null) {
                currentPosition = savedInstanceState.getInt("CurrentCity", 0);
            }

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                listCities.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                listCities.setItemChecked(currentPosition, true);
                showFragment();
            }
        }
    }

    private void showWeather() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            showFragment();
        }
        else{
            showActivity();
        }
    }

    private void showFragment() {

        FragmentManager fragmentManager = getFragmentManager();

        if(fragmentManager != null){
            Fragment fragment = getWeatherFragment();

            if(fragment != null){
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container_two, fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
            }
        }
    }

    private void showActivity() {
        Intent intent = new Intent(getActivity(), SecondActivity.class);
        intent.putExtra(keyForIndex, currentPosition);
        intent.putExtra(keyForAirHumidity, airHumidityFlag);
        intent.putExtra(keyForWindSpeed, windSpeedFlag);
        intent.putExtra(keyForPressure, pressureFlag);
        startActivity(intent);
    }

    private Fragment getWeatherFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager != null){
            Fragment fragment = fragmentManager.findFragmentById(R.id.main_container_two);
            if (fragment != null && !fragment.equals(new WeatherFragment())) {
                fragment = WeatherFragment.create(currentPosition, airHumidityFlag, windSpeedFlag, pressureFlag);
            } else {
                WeatherFragment weatherFragment = (WeatherFragment) fragmentManager.findFragmentById(R.id.main_container_two);
                if (weatherFragment == null || weatherFragment.getIndex() != currentPosition) {
                    fragment = WeatherFragment.create(currentPosition, airHumidityFlag, windSpeedFlag, pressureFlag);
                }
            }
            return fragment;
        }
        return null;
    }

    private void setClickListener(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = position;
                showWeather();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("CurrentCity", currentPosition);
        if (menu != null) {
            MenuItem airHumidity = menu.findItem(R.id.menu_air_humidity_check);
            outState.putBoolean(keyForAirHumidity, airHumidity.isChecked());

            MenuItem windSpeed = menu.findItem(R.id.menu_wind_speed_check);
            outState.putBoolean(keyForWindSpeed, windSpeed.isChecked());

            MenuItem pressure = menu.findItem(R.id.menu_pressure_check);
            outState.putBoolean(keyForPressure, pressure.isChecked());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem airHumidity = menu.findItem(R.id.menu_air_humidity_check);
        airHumidity.setChecked(airHumidityFlag);
        MenuItem windSpeed = menu.findItem(R.id.menu_wind_speed_check);
        windSpeed.setChecked(windSpeedFlag);
        MenuItem pressure = menu.findItem(R.id.menu_pressure_check);
        pressure.setChecked(pressureFlag);
        super.onPrepareOptionsMenu(menu);
    }

    private void getFlags (Bundle bundle) {
        if (bundle != null) {
            airHumidityFlag = bundle.getBoolean(keyForAirHumidity,
                    false);
            windSpeedFlag = bundle.getBoolean(keyForWindSpeed,
                    false);
            pressureFlag = bundle.getBoolean(keyForPressure,
                    false);
        }
    }
}
