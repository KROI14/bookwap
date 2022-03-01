package com.example.librarybookfinder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BorrowedBook{
    private int ID;
    private String title;
    private String author;
    private String yearPub;
    private String status;
    private String borrowType;
    private Date date;
    private String classify;

    public BorrowedBook(int ID, String title, String author, String yrPub, String status, String borrowType, Date date, String classify){
        this.ID = ID;
        this.title = title;
        this.author = author;
        this.yearPub = yrPub;
        this.status = status;
        this.borrowType = borrowType;
        this.date = date;
        this.classify = classify;
    }

    public int getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getYearPub() {
        return yearPub;
    }

    public String getStatus() {
        return status;
    }

    public String getBorrowType(){
        return borrowType;
    }

    public String getDate(){
        String strDate = new SimpleDateFormat("MMM dd").format(date);
        return strDate;
    }

    public String getClassification(){
        return classify;
    }
}
