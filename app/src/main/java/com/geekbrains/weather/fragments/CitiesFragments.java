package com.geekbrains.weather.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_sensors) {
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) {
                Fragment fragment = fragmentManager.findFragmentById(R.id.main_container);
                if (fragment != null) {
                    fragment = new SensorsFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_container, fragment)
                            .addToBackStack("Some_Key")
                            .commit();
                }
            }
        } else {
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
                fragment = WeatherFragment.create(currentPosition, getHumidity(), getWindSpeed(), getPressure());
            } else {
                WeatherFragment weatherFragment = (WeatherFragment) fragmentManager.findFragmentById(R.id.main_container);
                if (weatherFragment == null || weatherFragment.getIndex() != currentPosition) {
                    fragment = WeatherFragment.create(currentPosition, getHumidity(), getWindSpeed(), getPressure());
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
}
