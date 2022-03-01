package com.example.librarybookfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

class ListViewTask extends AsyncTask<Void, Bundle, Void> {
    private Intent intent;
    private Bundle bun;
    private ProgressDialog progressDialog;

    private int ID;
    private String classification;
    private Context con;

    public ListViewTask(int ID, String classification, Context con){
        this.ID = ID;
        this.classification = classification;
        this.con = con;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(con);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        intent = new Intent(con.getApplicationContext(), Result.class);
        bun = new Bundle();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(MainActivity.URL_BAHAY, "root", "");
            PreparedStatement prs = con.prepareStatement("SELECT * FROM `bookslibrary` WHERE classification = '" + classification + "'");
            ResultSet rs = prs.executeQuery();

            while(rs.next()){
                int ID = rs.getInt("BookID");
                if(this.ID == ID){
                    bun.putInt("bookID", ID);
                    bun.putString("Title", rs.getString(2));
                    bun.putString("Author", rs.getString(3));
                    bun.putString("YearPub", rs.getString(4));
                    bun.putString("Pub", rs.getString(5));
                    bun.putString("CallNum", rs.getString(6));
                    bun.putString("ISBN", rs.getString(7));
                    bun.putString("Lang", rs.getString(8));
                    bun.putString("Loc", rs.getString(9));
                    bun.putString("Acc", rs.getString(10));
                    bun.putString("Class", rs.getString(11));
                    bun.putString("Avail", rs.getString(12));
                    bun.putInt("Copies", rs.getInt(13));
                    bun.putString("TopSub", rs.getString(14));
                    bun.putByteArray("Img", rs.getBytes(15));
                    break;
                }
            }
            publishProgress(bun);
        }
        catch(Exception e){
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Bundle... values) {
        super.onProgressUpdate(values);
        intent.putExtras(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        con.startActivity(intent);
        progressDialog.dismiss();
    }
}

