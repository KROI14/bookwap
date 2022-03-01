package com.example.librarybookfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.qrcode.encoder.QRCode;

public class SplashScreen extends AppCompatActivity {

    private ImageView img;
    private boolean isBlack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedTheme sharedTheme = new SharedTheme(this);
        if(sharedTheme.isNightMode() ==  true){
            setTheme(R.style.DarkTheme);
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.resultQRStatusBar));
            isBlack = true;
        }
        else{
            setTheme(R.style.AppTheme);
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            isBlack = false;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        img = findViewById(R.id.imageView);

        if(isBlack){
            img.setImageDrawable(getResources().getDrawable(R.drawable.bookwaplogoblack));
        }
        else{
            img.setImageDrawable(getResources().getDrawable(R.drawable.bookwaplogowhite));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, QRScanner.class));
                finish();
            }
        }, 3000);
    }
}
