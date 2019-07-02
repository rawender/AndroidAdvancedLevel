package com.geekbrains.weather.fragments;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.util.Objects;

import static com.geekbrains.weather.fragments.WeatherFragment.keyForAirHumidity;
import static com.geekbrains.weather.fragments.WeatherFragment.keyForPressure;
import static com.geekbrains.weather.fragments.WeatherFragment.keyForWindSpeed;

public class CitiesFragments extends Fragment {

    public static final String keyForIndex = "index";

    private ListView listCities;
    private int currentPosition = 0;

    private Bundle savedInstanceState = null;

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
        getFlags(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
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
        final SharedPreferences defaultPrefs =
                PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()).getApplicationContext());

        if(fragmentActivity != null) {
            CitiesFragmentsAdapter adapter = new CitiesFragmentsAdapter(fragmentActivity,cities);
            listCities.setAdapter(adapter);
            setClickListener(listCities);

            if (savedInstanceState != null) {
                currentPosition = savedInstanceState.getInt("CurrentCity", 0);
            } else {
                currentPosition = getMyCityIndexFromPreference(defaultPrefs);
            }

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                listCities.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                listCities.setItemChecked(currentPosition, true);
                showFragment();
            }
        }
    }

    private void showFragment() {

        getFlags(savedInstanceState);

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

    private int getMyCityIndexFromPreference(SharedPreferences preferences) {
        int cityIndex;
        cityIndex = preferences.getInt(keyForIndex, 0);
        return cityIndex;
    }
}
