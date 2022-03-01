package com.example.librarybookfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String URL_BAHAY = "jdbc:mysql://192.168.1.10/library";

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ProgressBar progressBar;
    private FrameLayout frameLayout;

    private static ArrayList<Books> arrBooks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedTheme sharedTheme = new SharedTheme(this);
        if(sharedTheme.isNightMode() ==  true){
            setTheme(R.style.DarkTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.drawer_view);
        navigationView.setNavigationItemSelectedListener(new NavigationViewItemSelected());

        frameLayout = findViewById(R.id.fragment_container);
        progressBar = findViewById(R.id.progressBar);

        setImageToHeader();
        onCheckBooksIfCollected();

        if(savedInstanceState == null) {
            toolbar.setTitle("Search");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Search()).commit();
            navigationView.setCheckedItem(R.id.nav_search);
        }
    }

    public void onCheckBooksIfCollected(){
        if(arrBooks.size() == 0) {
            new GetBookTask().execute();
        }
        else{
            progressBar.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    public void hideKeyBoardOnTouch(View view) {
        Activity act = this;
        InputMethodManager keyBoard = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
        keyBoard.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), 0);
    }

    class GetBookTask extends AsyncTask<Void, Books, Void>{
        private String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arrBooks = new ArrayList<>();
            progressBar.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(URL_BAHAY, "root", "");
                PreparedStatement prs = con.prepareStatement("SELECT * FROM `bookslibrary` ORDER BY `BookTitle`");
                ResultSet rs = prs.executeQuery();

                while(rs.next()){
                    int id = rs.getInt(1);
                    String title = rs.getString(2);
                    String author = rs.getString(3);
                    String yrPub = rs.getString(4);
                    String classify = rs.getString(11);
                    byte[] img = rs.getBytes(15);

                    Books book = new Books(id, title, author, yrPub, classify, img);
                    publishProgress(book);
                }
                result = "Books Collected Successfully";
            }
            catch(Exception e){
                result = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Books... books) {
            super.onProgressUpdate(books);
            arrBooks.add(books[0]);
            progressBar.setProgress(arrBooks.size());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    public static ArrayList<Books> getBooks(){
        return arrBooks;
    }

    class NavigationViewItemSelected implements NavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch(menuItem.getItemId()){
                case R.id.nav_search:{
                    toolbar.setTitle("Search");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Search()).commit();
                }
                break;

                case R.id.nav_gen:{
                    toolbar.setTitle("General Works");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GenWorks()).commit();
                }
                break;

                case R.id.nav_philo:{
                    toolbar.setTitle("Philosophy ");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Philosophy()).commit();
                }
                break;

                case R.id.nav_religion:{
                    toolbar.setTitle("Religion");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Religion()).commit();
                }
                break;

                case R.id.nav_socScie:{
                    toolbar.setTitle("Social Sciences");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SocialSciences()).commit();
                }
                break;

                case R.id.nav_language:{
                    toolbar.setTitle("Language");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Language()).commit();
                }
                break;

                case R.id.nav_pureScience:{
                    toolbar.setTitle("Pure Science");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PureScience()).commit();
                }
                break;

                case R.id.nav_tech:{
                    toolbar.setTitle("Technology");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Technology()).commit();
                }
                break;

                case R.id.nav_arts:{
                    toolbar.setTitle("Arts and Recreation");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ArtsAndRecreation()).commit();
                }
                break;

                case R.id.nav_literature:{
                    toolbar.setTitle("Literature");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Literature()).commit();
                }
                break;

                case R.id.nav_history:{
                    toolbar.setTitle("History and Geography");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new History()).commit();
                }
                break;

                case R.id.nav_logs:{
                    toolbar.setTitle("Activity Logs");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ActivityLogs()).commit();
                }
                break;

                case R.id.nav_borrow:{
                    startActivity(new Intent(getApplicationContext(), BorrowLogs.class));
                }
                break;

                case R.id.nav_settings:{
                    toolbar.setTitle("Settings");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Settings()).commit();
                }
                break;

                case R.id.nav_logout:{
                    showLogOutDialog();
                }
                break;
            }
            drawer.closeDrawer(GravityCompat.START);

            return true;
        }
    }

    public void setImageToHeader(){
        View view = findViewById(R.id.drawer_view);
        ImageView img = view.findViewById(R.id.imgPic);
        TextView txtFName = view.findViewById(R.id.txtName);
        TextView txtCourse = view.findViewById(R.id.txtCourse);
        TextView txtStudNum = view.findViewById(R.id.txtStudNum);


        byte[] image = {};
        String fName = "";
        String course = "";
        String studNum = "";

        Bundle data = getIntent().getExtras();
        if(data != null){
            fName = data.getString("name");
            studNum = data.getString("num");
            course = data.getString("course");
            image = data.getByteArray("pic");
        }

        img.setImageBitmap(new OptimizeImage().optimizeBitmap(image, 0, 100, 100));
        txtFName.setText("Hi " + fName + "!");
        txtCourse.setText(course);
        txtStudNum.setText(studNum);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);

        MenuItem refresh = menu.findItem(R.id.option_refresh);
        MenuItem menuSearch = menu.findItem(R.id.option_search);
        refresh.setVisible(false);
        menuSearch.setVisible(true);
        return false;
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        if(navigationView.getCheckedItem().getItemId() == R.id.nav_borrow){
            navigationView.setCheckedItem(R.id.nav_search);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Search()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            showLogOutDialog();
        }
    }

    public void showLogOutDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure you want to sign out?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(MainActivity.this, QRScanner.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                finish();
            }
        });
        alert.setNegativeButton("No", null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }
}