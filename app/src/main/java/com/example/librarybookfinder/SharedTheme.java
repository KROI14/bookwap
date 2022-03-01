package com.example.librarybookfinder;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedTheme{

    private SharedPreferences sharedTheme;
    private SharedPreferences sharedSize;

    private static final String THEME = "theme";
    private static final String NIGHT = "night";

    private static final String SML = "sml";
    private static final String SIZE = "size";

    public SharedTheme(Context con){
        sharedTheme = con.getSharedPreferences(THEME, Context.MODE_PRIVATE);
        sharedSize = con.getSharedPreferences(SIZE, Context.MODE_PRIVATE);
    }

    public void setTextSize(float n){
        SharedPreferences.Editor sizeEdit = sharedSize.edit();
        sizeEdit.putFloat(SML, n);
        sizeEdit.commit();
    }

    public float textSize(){
        return  sharedSize.getFloat(SML, 16);
    }

    public void setNightMode(boolean b){
        SharedPreferences.Editor themeEdit = sharedTheme.edit();
        themeEdit.putBoolean(NIGHT, b);
        themeEdit.commit();
    }

    public boolean isNightMode(){
        return sharedTheme.getBoolean(NIGHT, false);
    }
}
