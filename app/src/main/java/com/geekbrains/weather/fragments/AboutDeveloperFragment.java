package com.geekbrains.weather.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.geekbrains.weather.R;

public class AboutDeveloperFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.about_developer_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem myCity = menu.findItem(R.id.menu_my_city);
        MenuItem history = menu.findItem(R.id.menu_history);
        MenuItem options = menu.findItem(R.id.options);
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
        }
    }
}
