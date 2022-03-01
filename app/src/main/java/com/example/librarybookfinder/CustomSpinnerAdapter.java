package com.example.librarybookfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomSpinnerAdapter extends ArrayAdapter<String>{

    public CustomSpinnerAdapter(Context con, String[] items){
        super(con, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner, parent, false);
        }

        TextView txtActType = convertView.findViewById(R.id.txtActType);

        String actType = getItem(position);
        txtActType.setText(actType);

        return convertView;
    }
}
