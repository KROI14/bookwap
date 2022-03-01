package com.example.librarybookfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BorrowLogs extends AppCompatActivity {

    private BottomNavigationView bottomView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedTheme sharedTheme = new SharedTheme(this);
        if(sharedTheme.isNightMode() ==  true){
            setTheme(R.style.DarkTheme);
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.resultQRStatusBar));
        }
        else{
            setTheme(R.style.AppTheme);
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_logs);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bottomView = findViewById(R.id.bottomNav);
        bottomView.setOnNavigationItemSelectedListener(bottomNavListener);

        toolbar.setTitle("Borrow Inside");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BorrowInside()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFrag = null;

                    switch (menuItem.getItemId()){
                        case R.id.bottom_in:{
                            toolbar.setTitle("Borrow Inside");
                            selectedFrag = new BorrowInside();
                        }
                        break;

                        case R.id.bottom_out:{
                            toolbar.setTitle("Borrow Outside");
                            selectedFrag = new BorrowOutside();
                        }
                        break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFrag).commit();

                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);

        MenuItem refresh = menu.findItem(R.id.option_refresh);
        MenuItem menuSearch = menu.findItem(R.id.option_search);
        refresh.setVisible(true);
        menuSearch.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                onBackPressed();
            }
            break;
        }
        return(super.onOptionsItemSelected(item));
    }

}
