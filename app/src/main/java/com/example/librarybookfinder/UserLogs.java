package com.example.librarybookfinder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserLogs {
    private String book;
    private Date time;
    private Date date;

    public UserLogs(String book, Date date, Date time) {
        this.book = book;
        this.date = date;
        this.time = time;
    }

    public String getBook() {
        return book;
    }

    public String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyy");
        String date = dateFormat.format(this.date);

        return date;
    }

    public String getTime() {
        DateFormat timeFormat = new SimpleDateFormat("hh:mm:aa");
        String time = timeFormat.format(this.time);

        return time;
    }
}
