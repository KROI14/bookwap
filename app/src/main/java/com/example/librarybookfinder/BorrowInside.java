package com.example.librarybookfinder;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

public class BorrowInside extends Fragment implements AdapterView.OnItemSelectedListener{

    private View view;
    private ListView listView;
    private ArrayList<BorrowedBook> borrowedBooks;
    private Spinner spnStatus;

    private String[] status = {"Pending", "Available", "On Use", "Returned", "Invalid"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ContextThemeWrapper theme;
        SharedTheme sharedTheme = new SharedTheme(getContext());
        if(sharedTheme.isNightMode() == true){
            theme = new ContextThemeWrapper(getContext(), R.style.DarkTheme);
        }
        else{
            theme = new ContextThemeWrapper(getContext(), R.style.AppTheme);
        }
        view = inflater.cloneInContext(theme).inflate(R.layout.fragment_borrow_inside, container, false);
        setHasOptionsMenu(true);

        spnStatus = view.findViewById(R.id.spnStatus);
        listView = view.findViewById(R.id.listView);

        spnStatus.setOnItemSelectedListener(this);
        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(getContext(), status);
        spnStatus.setAdapter(spinAdapter);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String status = parent.getItemAtPosition(position).toString();
        new Task(status).execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.option_refresh:{
                new Task(spnStatus.getSelectedItem().toString()).execute();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    class Task extends AsyncTask<Void, BorrowedBook, Void> {

        private String res;
        private ProgressDialog progressDialog = new ProgressDialog(getContext());
        private String status;

        public Task(String status){
            this.status = status;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            borrowedBooks = new ArrayList<BorrowedBook>();
            progressDialog.setTitle("Please wait...");
            progressDialog.setMessage("Collecting data");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try{
                Connection con = DriverManager.getConnection(MainActivity.URL_BAHAY, "root", "");
                PreparedStatement prs = con.prepareStatement("SELECT bookslibrary.BookID, bookslibrary.BookTitle, bookslibrary.BookAuthor, bookslibrary.YearPub, bookslibrary.Classification, request.Date, request.Status,  request.BorrowType FROM bookslibrary INNER JOIN request on bookslibrary.BookID = request.BookID WHERE request.UserID = " + QRScanner.UserID + " AND request.BorrowType = 'Inside' AND request.Status = '"+ this.status +"'");
                ResultSet rs = prs.executeQuery();

                while(rs.next()){
                    int bookID = rs.getInt("BookID");
                    String title = rs.getString("BookTitle");
                    String author = rs.getString("BookAuthor");
                    String yrPub = rs.getString("YearPub");
                    String classif = rs.getString("Classification");
                    Date date = rs.getDate("Date");
                    String status = rs.getString("Status");
                    String borrowType = rs.getString("BorrowType");

                    publishProgress(new BorrowedBook(bookID, title, author, yrPub, status, borrowType, date, classif));
                }
            }
            catch(Exception e){
                res = e.getMessage();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(BorrowedBook... values) {
            super.onProgressUpdate(values);
            borrowedBooks.add(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listView.setAdapter(new BorrowListViewAdapter(getContext(), R.layout.custom_borrow_list, borrowedBooks));
            progressDialog.dismiss();
        }
    }
}
