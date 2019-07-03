package com.geekbrains.weather.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.geekbrains.weather.R;

public class FeedbackFragment extends Fragment {

    private EditText editText;
    private Button buttonSend;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feedback_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        initViews(view);
        setOnClickSendBtn();
    }

    private void initViews(View view) {
        editText = view.findViewById(R.id.edit_text);
        buttonSend = view.findViewById(R.id.btm_send);
    }

    private void setOnClickSendBtn() {
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
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
