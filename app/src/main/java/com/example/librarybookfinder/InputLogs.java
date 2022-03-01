package com.example.librarybookfinder;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class InputLogs extends AsyncTask<Void, Void, Void> {
    private String sql;

    public InputLogs(String sql){
        this.sql = sql;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(MainActivity.URL_BAHAY, "root", "");
            PreparedStatement prs = con.prepareStatement(sql);
            prs.executeUpdate();
        }
        catch (Exception e){
            e.getMessage();
        }

        return null;
    }
}
