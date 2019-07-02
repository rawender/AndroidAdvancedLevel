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
import com.geekbrains.weather.support.WeatherHistoryAdapter;

public class WeatherHistoryFragment extends Fragment {

    public final static String keyForCityName = "keyForCityName";
    public final static String keyForCityTempHistory = "keyForCityTempHistory";

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
        actRecView();
    }

    private void actRecView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        WeatherHistoryAdapter adapter = new WeatherHistoryAdapter(tempHistory);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initView(View view) {
        cityName = view.findViewById(R.id.cityName);
        recyclerView = view.findViewById(R.id.recyclerView);
    }


    private void setCityName() {
        if (getArguments() != null) {
            cityName.append(getArguments().getString(keyForCityName));
        }
    }

    private void setTempHistory() {
        if (getArguments() != null) {
            tempHistory = getArguments().getStringArray(keyForCityTempHistory);
        }
    }
}
