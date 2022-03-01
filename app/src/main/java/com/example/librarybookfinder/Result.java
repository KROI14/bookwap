package com.example.librarybookfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Result extends AppCompatActivity {

    private TextView titleTitle, authorTitle, yrPubTitle, publisherTitle, callNumTitle, isbnTitle,
            langTitle, locationTitle, accessionTitle, classificationTitle, availTitle, topSubTitle;

    private TextView txtTitle, txtAuthor, txtYrPub, txtPublisher, txtCallNum, txtIsbn, txtLang, txtLoc,
            txtAccession, txtClassification, txtAvail, txtTopSub;

    private ImageView bookImage;

    private Button btnBorrow;

    private int ID;

    private String selectedType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState){
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
        setContentView(R.layout.activity_result);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Book Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textInitialization();
        textTitleInitialization();
        changeFontSize(sharedTheme.textSize());

        Intent intent = getIntent();
        String title = "";
        String author = "";
        String yrPub = "";
        String pub = "";
        String callNum = "";
        String isbn = "";
        String language = "";
        String location = "";
        String access = "";
        String classification = "";
        String avail = "";
        int copies = 0;
        String topSub = "";
        byte[] img = {};

        if(intent.getExtras() !=  null){
            ID = intent.getIntExtra("bookID", ID);
            title = intent.getStringExtra("Title");
            author = intent.getStringExtra("Author");
            yrPub = intent.getStringExtra("YearPub");
            pub = intent.getStringExtra("Pub");
            callNum = intent.getStringExtra("CallNum");
            isbn = intent.getStringExtra("ISBN");
            language = intent.getStringExtra("Lang");
            location = intent.getStringExtra("Loc");
            access = intent.getStringExtra("Acc");
            classification = intent.getStringExtra("Class");
            avail = intent.getStringExtra("Avail");
            copies = intent.getIntExtra("Copies", copies);
            topSub = intent.getStringExtra("TopSub");
            img = intent.getByteArrayExtra("Img");
        }

        Bitmap image = new OptimizeImage().optimizeBitmap(img, 0, 100, 100);

        txtTitle.setText(title);
        txtAuthor.setText(author);
        txtYrPub.setText(yrPub);
        txtPublisher.setText(pub);
        txtCallNum.setText(callNum);
        txtIsbn.setText(isbn);
        txtLang.setText(language);
        txtLoc.setText(location);
        txtAccession.setText(access);
        txtClassification.setText(classification);
        txtAvail.setText(avail + " Copies(" + copies + ")");
        txtTopSub.setText(topSub);
        bookImage.setImageBitmap(image);

        if(copies == 0){
            btnBorrow.setEnabled(false);
        }

        new InputLogs("INSERT INTO `viewedbooks`(`UserID`, `Book`) VALUES (" + QRScanner.UserID + ",'" + title + "')").execute();
    }

    public void borrowBook(View v)
    {
        final String[] types = {"Inside", "Outside"};

        AlertDialog.Builder borrowType = new AlertDialog.Builder(this);
        borrowType.setTitle("Where will you use the book?");
        borrowType.setSingleChoiceItems(types, -1, new AlertDialog.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedType = types[which];
            }
        });

        borrowType.setPositiveButton("OK", new AlertDialog.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(selectedType.isEmpty())
                {
                    AlertDialog.Builder note = new AlertDialog.Builder(Result.this);
                    note.setMessage("Please choose between the 2 buttons");
                    note.setPositiveButton("OK", null);
                    note.show();
                }
                else {
                    new Borrow(ID, selectedType).execute();
                }
            }
        });

        borrowType.setNegativeButton("Cancel", null);
        borrowType.show();
    }

    class Borrow extends AsyncTask<Void, Void, Void>
    {
        private int bookID = 0;
        private String borrowType = "";

        private String res;

        private Connection con;
        private PreparedStatement prs;
        private ResultSet rs;

        private ProgressDialog progress = new ProgressDialog(Result.this);

        public Borrow(int bookID, String borrowType)
        {
            this.bookID = bookID;
            this.borrowType = borrowType;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progress.setTitle("Processing");
            progress.setMessage("please wait...");
            progress.show();
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try
            {
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection(MainActivity.URL_BAHAY, "root", "");
                if(borrowType.equals("Outside")) {
                    prs = con.prepareStatement("SELECT count(*) FROM request WHERE UserID = " + QRScanner.UserID + " AND BorrowType = 'Outside' AND (Status != 'Returned' OR 'Invalid')");
                    rs = prs.executeQuery();
                    int rows = 0;
                    while (rs.next()) {
                        rows = rs.getInt(1);
                    }
                    if (rows >= 2) {
                        res = "You have reached the maximum amount of books that can be borrow outside";
                    }
                    else {
                        int expr = 0;
                        prs = con.prepareStatement("SELECT * FROM request");
                        rs = prs.executeQuery();
                        while (rs.next()) {
                            int book = rs.getInt("BookID");
                            int userID = rs.getInt("UserID");
                            String status = rs.getString("Status");

                            if ((book == this.bookID) && (userID == QRScanner.UserID)) {
                                if (status.equals("Pending")) {
                                    expr = 1;
                                } else if (status.equals("Available")) {
                                    expr = 2;
                                } else if (status.equals("On Use")) {
                                    expr = 3;
                                }
                            }
                        }

                        if (expr == 1) {
                            res = "Book is on pending please wait till the status becomes available";
                        } else if (expr == 2) {
                            res = "The book is already available for you";
                        } else if (expr == 3) {
                            res = "You still have the book";
                        } else {
                            prs = con.prepareStatement("INSERT INTO request(BookID, UserID, BorrowType) VALUES(?, ?, ?)");
                            prs.setInt(1, this.bookID);
                            prs.setInt(2, QRScanner.UserID);
                            prs.setString(3, this.borrowType);
                            prs.executeUpdate();

                            res = "The book successfully queued";
                        }
                    }
                }
                else{
                    int expr = 0;
                    prs = con.prepareStatement("SELECT * FROM request");
                    rs = prs.executeQuery();
                    while (rs.next()) {
                        int book = rs.getInt("BookID");
                        int userID = rs.getInt("UserID");
                        String status = rs.getString("Status");

                        if ((book == this.bookID) && (userID == QRScanner.UserID)) {
                            if (status.equals("Pending")) {
                                expr = 1;
                            } else if (status.equals("Available")) {
                                expr = 2;
                            } else if (status.equals("On Use")) {
                                expr = 3;
                            }
                        }
                    }

                    if (expr == 1) {
                        res = "Book is on pending please wait till the status becomes available";
                    } else if (expr == 2) {
                        res = "The book is already available for you";
                    } else if (expr == 3) {
                        res = "You still have the book";
                    } else {
                        prs = con.prepareStatement("INSERT INTO request(BookID, UserID, BorrowType) VALUES(?, ?, ?)");
                        prs.setInt(1, this.bookID);
                        prs.setInt(2, QRScanner.UserID);
                        prs.setString(3, this.borrowType);
                        prs.executeUpdate();

                        res = "The book successfully queued";
                    }
                }
            }
            catch(Exception ex)
            {
                res = ex.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            progress.dismiss();

            AlertDialog.Builder note = new AlertDialog.Builder(Result.this);
            note.setMessage(res);
            note.setPositiveButton("OK", null);
            note.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                onBackPressed();
            }
        }

        return(super.onOptionsItemSelected(item));
    }

    public void textInitialization(){
        txtTitle = findViewById(R.id.txtTitle);
        txtAuthor = findViewById(R.id.txtAuthor);
        txtYrPub = findViewById(R.id.txtYrPub);
        txtPublisher = findViewById(R.id.txtPublisher);
        txtCallNum = findViewById(R.id.txtCallNum);
        txtIsbn = findViewById(R.id.txtISBN);
        txtLang = findViewById(R.id.txtLang);
        txtLoc = findViewById(R.id.txtLocation);
        txtAccession = findViewById(R.id.txtAccession);
        txtClassification = findViewById(R.id.txtClassification);
        txtAvail = findViewById(R.id.txtAvail);
        txtTopSub = findViewById(R.id.txtTopSub);
        bookImage = findViewById(R.id.bookImage);
    }

    public void textTitleInitialization(){
        titleTitle = findViewById(R.id.title_txtTitle);
        authorTitle = findViewById(R.id.title_txtAuthor);
        yrPubTitle = findViewById(R.id.title_txtYrPub);
        publisherTitle = findViewById(R.id.title_txtPublisher);
        callNumTitle = findViewById(R.id.title_txtCallNum);
        isbnTitle = findViewById(R.id.title_txtISBN);
        langTitle = findViewById(R.id.title_txtLang);
        locationTitle = findViewById(R.id.title_txtLocation);
        accessionTitle = findViewById(R.id.title_txtAccession);
        classificationTitle = findViewById(R.id.title_txtClassification);
        availTitle = findViewById(R.id.title_txtAvail);
        topSubTitle = findViewById(R.id.title_txtTopSub);

        btnBorrow = findViewById(R.id.btnBorrow);
    }

    public void changeFontSize(float n){
        titleTitle.setTextSize(n);
        authorTitle.setTextSize(n);
        yrPubTitle.setTextSize(n);
        publisherTitle.setTextSize(n);
        callNumTitle.setTextSize(n);
        isbnTitle.setTextSize(n);
        langTitle.setTextSize(n);
        locationTitle.setTextSize(n);
        accessionTitle.setTextSize(n);
        classificationTitle.setTextSize(n);
        availTitle.setTextSize(n);
        topSubTitle.setTextSize(n);

        txtTitle.setTextSize(n);
        txtAuthor.setTextSize(n);
        txtYrPub.setTextSize(n);
        txtPublisher.setTextSize(n);
        txtCallNum.setTextSize(n);
        txtIsbn.setTextSize(n);
        txtLang.setTextSize(n);
        txtLoc.setTextSize(n);
        txtAccession.setTextSize(n);
        txtClassification.setTextSize(n);
        txtAvail.setTextSize(n);
        txtTopSub.setTextSize(n);
    }
}