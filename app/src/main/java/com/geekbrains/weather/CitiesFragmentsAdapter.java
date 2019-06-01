package com.geekbrains.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CitiesFragmentsAdapter extends BaseAdapter {
    private Context context;
    private String[] cityNames;

    public CitiesFragmentsAdapter(@NonNull Context context, String[] cityNames) {
        this.context = context;
        this.cityNames = cityNames;
    }

    @Override
    public int getCount() {
        return cityNames.length;
    }

    @Override
    public String getItem(int position) {
        return cityNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String item = getItem(position);
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.element, parent, false);

        initText(convertView, item);

        return convertView;
    }

    private void initText(@Nullable View convertView, String item) {
        TextView textView = null;
        if (convertView != null) {
            textView = convertView.findViewById(R.id.textView);
        }
        if (textView != null) {
            textView.setText(item);
        }
    }
}
