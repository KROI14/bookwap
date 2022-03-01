package com.example.librarybookfinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class Settings extends Fragment {

    View view;

    private RadioGroup rbGroup;
    private RadioButton rbSmall, rbMed, rbLarge;
    private Switch nightSwitch;
    private Button btnApply;
    private TextView txtTitleFontSize, txtTitleDarkMode;

    private SharedTheme sharedTheme;

    private float size;
    private boolean night;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ContextThemeWrapper theme;
        sharedTheme = new SharedTheme(getContext());
        if(sharedTheme.isNightMode() == true){
            theme = new ContextThemeWrapper(getContext(), R.style.DarkTheme);
        }
        else{
            theme = new ContextThemeWrapper(getContext(), R.style.AppTheme);
        }
        view =inflater.cloneInContext(theme).inflate(R.layout.fragment_settings, container, false);
        setHasOptionsMenu(false);

        rbGroup = view.findViewById(R.id.rbGroup);
        rbSmall = view.findViewById(R.id.rbSmall);
        rbMed = view.findViewById(R.id.rbMedium);
        rbLarge = view.findViewById(R.id.rbLarge);
        nightSwitch = view.findViewById(R.id.nightSwitch);
        btnApply = view.findViewById(R.id.btnApply);
        txtTitleFontSize = view.findViewById(R.id.title_fontSize);
        txtTitleDarkMode = view.findViewById(R.id.title_nightMode);

        if(sharedTheme.textSize() == 16){
            rbSmall.setChecked(true);
            rbMed.setChecked(false);
            rbLarge.setChecked(false);
            size = 16;
        }
        else if(sharedTheme.textSize() == 20){
            rbSmall.setChecked(false);
            rbMed.setChecked(true);
            rbLarge.setChecked(false);
            size = 20;
        }
        else if(sharedTheme.textSize() == 24){
            rbSmall.setChecked(false);
            rbMed.setChecked(false);
            rbLarge.setChecked(true);
            size = 24;
        }
        rbGroup.setOnCheckedChangeListener(new RadioListener());

        if(sharedTheme.isNightMode() == true){
            nightSwitch.setChecked(true);
        }
        nightSwitch.setOnCheckedChangeListener(new SwitchListener());

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedTheme.setTextSize(size);
                sharedTheme.setNightMode(night);
                startActivity(new Intent(getContext(), QRScanner.class));
                getActivity().finish();
            }
        });
        changeFontSize(sharedTheme.textSize());

        return view;
    }

    class RadioListener implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch (radioGroup.getCheckedRadioButtonId()){
                case R.id.rbSmall:{
                    size = 16;
                }
                break;

                case R.id.rbMedium:{
                    size = 20;
                }
                break;

                case R.id.rbLarge:{
                    size = 24;
                }
                break;
            }
        }
    }

    class SwitchListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b){
                night = true;
            }
            else{
                night = false;
            }
        }
    }

    public void changeFontSize(float n){
        rbSmall.setTextSize(n);
        rbMed.setTextSize(n);
        rbLarge.setTextSize(n);
        txtTitleDarkMode.setTextSize(n);
        txtTitleFontSize.setTextSize(n);
        nightSwitch.setTextSize(n);
    }
}
