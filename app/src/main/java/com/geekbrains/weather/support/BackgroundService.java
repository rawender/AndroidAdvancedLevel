package com.geekbrains.weather.support;

import android.app.IntentService;
import android.content.Intent;

import com.geekbrains.weather.R;

import static com.geekbrains.weather.fragments.CitiesFragments.keyForIndex;
import static com.geekbrains.weather.fragments.WeatherHistoryFragment.keyForCityName;
import static com.geekbrains.weather.fragments.WeatherHistoryFragment.keyForCityTempHistory;


public class BackgroundService extends IntentService {

    public final static String BROADCAST_ACTION = "Key fo service";
    private String[] tempHistory;
    private String cityName;
    private int index;

    public BackgroundService() {
        super("background_service_weather");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        index = intent.getIntExtra(keyForIndex, 0);
        setCityName();
        setTempHistory();
        Intent broadcastIntent = new Intent(BROADCAST_ACTION);
        broadcastIntent.putExtra(keyForCityName, cityName);
        broadcastIntent.putExtra(keyForCityTempHistory, tempHistory);
        sendBroadcast(broadcastIntent);
    }

    private void setCityName() {
        String[] cityNames = getResources().getStringArray(R.array.cities);
        cityName = cityNames[index];
    }

    private void setTempHistory() {
        String[] cityId = getResources().getStringArray(R.array.cities_id);
        switch (cityId[index]) {
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
