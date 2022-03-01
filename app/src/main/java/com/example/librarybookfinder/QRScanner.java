package com.example.librarybookfinder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.Result;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView scannerView;
    private ArrayList<Users> arrUser = new ArrayList<>();

    private WifiManager wifi;

    public static int UserID;

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

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        scannerView.setResultHandler(this);
        scannerView.startCamera();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);
        }

        wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifi.isWifiEnabled()){
            new UserConnection().execute();
        }
        else{
            showTurnOnWifiPopUp();
        }
    }

    @Override
    public void handleResult(Result result){
        try {
            String ID = result.getText();
            intentToMain(ID);
        }
        catch(Exception e){
            Toast.makeText(this, "Scan another QR", Toast.LENGTH_SHORT).show();
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        }
    }

    public void intentToMain(String studID){
        Intent intent = new Intent(this, MainActivity.class);
        String res = "";

        if(!arrUser.isEmpty()) {
            for (int i = 0; i < arrUser.size(); i++) {
               if(arrUser.get(i).getID() == Integer.parseInt(studID)) {
                    intent.putExtra("name", arrUser.get(i).getfName());
                    intent.putExtra("num", arrUser.get(i).getStudNum());
                    intent.putExtra("course", arrUser.get(i).getCourse());
                    intent.putExtra("pic", arrUser.get(i).getImg());

                    UserID = arrUser.get(i).getID();

                    res = "Successfully Logged In";

                    startActivity(intent);
                    finish();
                    scannerView.stopCamera();
                    break;
               }
               else{
                    res = "Scan another QR";
                    scannerView.setResultHandler(this);
                    scannerView.startCamera();
               }
            }
            Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Connect to the Wifi library and restart the application before scanning", Toast.LENGTH_LONG).show();
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        }
    }

    class UserConnection extends AsyncTask<Void, Users, Void>{
        private String result;
        private ProgressDialog progressDialog = new ProgressDialog(QRScanner.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arrUser = new ArrayList();
            progressDialog.setTitle("Connecting to the database");
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(MainActivity.URL_BAHAY, "root", "");
                PreparedStatement prs = con.prepareStatement("SELECT * FROM `user`");
                ResultSet rs = prs.executeQuery();

                while(rs.next()){
                    int ID = rs.getInt(1);
                    String fName = rs.getString(2);
                    String studNum = rs.getString(4);
                    String course = rs.getString(5);
                    byte[] img = rs.getBytes(6);

                    publishProgress(new Users(ID, fName, studNum, course, img));
                }
                result = "Scan a QR Code to login";
            }
            catch(Exception e){
                result = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Users... values) {
            super.onProgressUpdate(values);
            arrUser.add(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            Toast.makeText(QRScanner.this, result, Toast.LENGTH_SHORT).show();
        }
    }


    private void showTurnOnWifiPopUp(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Before using the application you need to turn on your wifi and start the app again\n\nwould you like to open the wifi?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        wifi.setWifiEnabled(true);
                        finish();
                    }
                });
        alert.setNegativeButton("No", null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onStart() {
        super.onStart();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
}
