package com.geekbrains.weather.fragments;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.geekbrains.weather.R;
import com.geekbrains.weather.support.CitiesFragmentsAdapter;

import static com.geekbrains.weather.fragments.WeatherFragment.keyForAirHumidity;
import static com.geekbrains.weather.fragments.WeatherFragment.keyForPressure;
import static com.geekbrains.weather.fragments.WeatherFragment.keyForWindSpeed;

public class CitiesFragments extends Fragment {

    public static final String keyForIndex = "index";

    private ListView listCities;
    private int currentPosition;

    private boolean airHumidityFlag;
    private boolean windSpeedFlag;
    private boolean pressureFlag;

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
        getFlags();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int id = item.getItemId();
            switch (id) {
                case R.id.menu_air_humidity_check:
                    item.setChecked(!item.isChecked());
                    airHumidityFlag = item.isChecked();
                    showFragment();
                    break;
                case R.id.menu_wind_speed_check:
                    item.setChecked(!item.isChecked());
                    windSpeedFlag = item.isChecked();
                    showFragment();
                    break;
                case R.id.menu_pressure_check:
                    item.setChecked(!item.isChecked());
                    pressureFlag = item.isChecked();
                    showFragment();
                    break;
                default:
                    return false;
            }
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
        }
    }

    private void showFragment() {

        FragmentManager fragmentManager = getFragmentManager();

        if(fragmentManager != null){
            Fragment fragment = getWeatherFragment();

            if(fragment != null){
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
            }
        }
    }

    private Fragment getWeatherFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager != null){
            Fragment fragment = fragmentManager.findFragmentById(R.id.main_container);
            if (fragment != null && !fragment.equals(new WeatherFragment())) {
                fragment = WeatherFragment.create(currentPosition, airHumidityFlag, windSpeedFlag, pressureFlag);
            } else {
                WeatherFragment weatherFragment = (WeatherFragment) fragmentManager.findFragmentById(R.id.main_container);
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
                showFragment();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("CurrentCity", currentPosition);
        super.onSaveInstanceState(outState);
    }

    private void getFlags () {
        if (getArguments() != null) {
            airHumidityFlag = getArguments().getBoolean(keyForAirHumidity, false);
            windSpeedFlag = getArguments().getBoolean(keyForWindSpeed, false);
            pressureFlag = getArguments().getBoolean(keyForPressure, false);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            if (this.isVisible()) {
                MenuItem options = menu.findItem(R.id.options);
                if (options != null) {
                    options.setVisible(false);
                }
                MenuItem listCities = menu.findItem(R.id.menu_list_of_cities);
                if (listCities != null) {
                    listCities.setVisible(false);
                }
                MenuItem history = menu.findItem(R.id.menu_history);
                if (history != null) {
                    history.setVisible(false);
                }
                MenuItem myCity = menu.findItem(R.id.menu_my_city);
                if (myCity != null) {
                    myCity.setVisible(false);
                }
            }
        }
    }
}
