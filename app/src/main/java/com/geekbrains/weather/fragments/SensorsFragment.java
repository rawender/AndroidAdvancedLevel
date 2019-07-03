package com.geekbrains.weather.fragments;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geekbrains.weather.R;

import java.util.Objects;

import static android.content.Context.SENSOR_SERVICE;

public class SensorsFragment extends Fragment {

    private TextView textTemperature;
    private TextView textHumidity;
    private SensorManager sensorManager;
    private Sensor sensorTemperature;
    private Sensor sensorHumidity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sensors_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        initViews(view);
        getSensors();
    }

    private void initViews(View view) {
        textTemperature = view.findViewById(R.id.temperature_data);
        textHumidity = view.findViewById(R.id.humidity_data);
    }

    private void getSensors() {
        sensorManager = (SensorManager) Objects.requireNonNull(getActivity()).getSystemService(SENSOR_SERVICE);
        sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        sensorManager.registerListener(listenerTemperature, sensorTemperature,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listenerHumidity, sensorHumidity,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listenerTemperature, sensorTemperature);
        sensorManager.unregisterListener(listenerHumidity, sensorHumidity);
    }

    private void showLightSensors(SensorEvent event, TextView text){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(event.values[0]);
        text.setText(stringBuilder);
    }

    SensorEventListener listenerTemperature = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        @Override
        public void onSensorChanged(SensorEvent event) {
            showLightSensors(event, textTemperature);
        }
    };

    SensorEventListener listenerHumidity = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        @Override
        public void onSensorChanged(SensorEvent event) {
            showLightSensors(event, textHumidity);
        }
    };

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem options = menu.findItem(R.id.options);
        MenuItem history = menu.findItem(R.id.menu_history);
        MenuItem myCity = menu.findItem(R.id.menu_my_city);
        MenuItem sensors = menu.findItem(R.id.menu_sensors);
        if (this.isVisible()) {
            if (options != null) {
                options.setVisible(false);
            }
            if (history != null) {
                history.setVisible(false);
            }
            if (myCity != null) {
                myCity.setVisible(false);
            }
            if (sensors != null) {
                sensors.setVisible(false);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
