package com.example.librarybookfinder;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActivityLogs extends Fragment {

    private View view;

    private Button btnShowDate;
    private TextView txtViewedActs, txtDate;
    private Spinner spinActType;

    private String sql, date;
    private String[] actType = {"Searched Title", "Searched Author", "Viewed Books"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ContextThemeWrapper theme;
        SharedTheme sharedTheme = new SharedTheme(getContext());
        if(sharedTheme.isNightMode() == true){
            theme = new ContextThemeWrapper(getContext(), R.style.DarkTheme);
        }
        else{
            theme = new ContextThemeWrapper(getContext(), R.style.AppTheme);
        }
        view =inflater.cloneInContext(theme).inflate(R.layout.fragment_activitylogs, container, false);
        setHasOptionsMenu(false);

        Date currentDate = new Date();
        date = new SimpleDateFormat("MMM dd, yyy").format(currentDate);

        spinActType = view.findViewById(R.id.spinActType);
        spinActType.setOnItemSelectedListener(new SpinnerFunction());
        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(getContext(), actType);
        spinActType.setAdapter(spinAdapter);

        btnShowDate = view.findViewById(R.id.btnShowDate);

        txtViewedActs = view.findViewById(R.id.txtViewedBooks);
        txtDate = view.findViewById(R.id.txtDate);

        btnShowDate.setOnClickListener(new ButtonDatePicker());
        return view;
    }

    class SpinnerFunction implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String selectedStr = (String)adapterView.getItemAtPosition(i);
            if(selectedStr.equals(actType[0])){
                sql = "searchedtitle";
            }
            else if(selectedStr.equals(actType[1])){
                sql = "searchedauthor";
            }
            else if(selectedStr.equals(actType[2])){
                sql = "viewedbooks";
            }
            new GetData(sql , date).execute();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    class ButtonDatePicker implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            final Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    cal.set(Calendar.YEAR, i);
                    cal.set(Calendar.MONTH, i1);
                    cal.set(Calendar.DAY_OF_MONTH, i2);
                    date = new SimpleDateFormat("MMM dd, yyy").format(cal.getTime());
                    new GetData(sql , date).execute();
                }
            }, year, month, day);
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();
        }
    }

    class GetData extends AsyncTask<Void, UserLogs, Void> {
        private ArrayList<UserLogs> arr;
        private ProgressDialog progressDialog = new ProgressDialog(getContext());

        private String acType, date;

        public GetData(String actType, String date){
            this.acType = actType;
            this.date = date;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arr = new ArrayList<>();
            progressDialog.setTitle("Collecting Activity Logs");
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            txtViewedActs.setText("");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(MainActivity.URL_BAHAY, "root", "");
                PreparedStatement prs = con.prepareStatement("SELECT * FROM `" + this.acType + "` WHERE UserID =" + QRScanner.UserID);
                ResultSet rs = prs.executeQuery();

                while(rs.next()){
                    String data = rs.getString(3);
                    Time time = rs.getTime(4);
                    Date dates = rs.getDate(5);

                    publishProgress(new UserLogs(data, dates, time));
                }
            }
            catch (Exception e){
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(UserLogs... values) {
            super.onProgressUpdate(values);
            arr.add(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            txtDate.setText(this.date);
            for(int i = 0; i < arr.size(); i++){
                String activity = arr.get(i).getBook();
                String time = arr.get(i).getTime();
                String dates = arr.get(i).getDate();

                if(txtDate.getText().toString().equals(dates)) {
                    txtViewedActs.append(activity + " (" + time + "/" + dates + ")\n\n");
                }
            }
            progressDialog.dismiss();
        }
    }
}
