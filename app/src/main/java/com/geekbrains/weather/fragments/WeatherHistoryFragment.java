package com.geekbrains.weather.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geekbrains.weather.R;
import com.geekbrains.weather.WeatherHistoryAdapter;

import static com.geekbrains.weather.fragments.CitiesFragments.keyForIndex;

public class WeatherHistoryFragment extends Fragment {

    private String[] tempHistory;
    private TextView cityName;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.weather_history_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTempHistory();
        initView(view);
        setCityName();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        WeatherHistoryAdapter adapter = new WeatherHistoryAdapter(tempHistory);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public int getIndex() {
        int index = 0;
        if (getArguments() != null) {
            index = getArguments().getInt(keyForIndex, 0);
        }
        return index;
    }

    private void initView(View view) {
        cityName = view.findViewById(R.id.cityName);
        recyclerView = view.findViewById(R.id.recyclerView);
    }


    private void setCityName() {
        String[] cityNames = getResources().getStringArray(R.array.cities);
        cityName.setText(cityNames[getIndex()]);
    }

    private void setTempHistory() {
        String[] cityId = getResources().getStringArray(R.array.cities_id);
        switch (cityId[getIndex()]) {
            case "Moscow_id": {
                tempHistory = getResources().getStringArray(R.array.Moscow_id);
                break;
            }
            case "St_Petersburg_id": {
                tempHistory = getResources().getStringArray(R.array.St_Petersburg_id);
                break;
            }
            case "Yekaterinburg_id": {
                tempHistory = getResources().getStringArray(R.array.Yekaterinburg_id);
                break;
            }
            case "Novosibirsk_id": {
                tempHistory = getResources().getStringArray(R.array.Novosibirsk_id);
                break;
            }
            case "Samara_id": {
                tempHistory = getResources().getStringArray(R.array.Samara_id);
                break;
            }
        }
    }
}
