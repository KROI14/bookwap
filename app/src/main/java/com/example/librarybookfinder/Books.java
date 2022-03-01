package com.example.librarybookfinder;

public class Books {

    private int ID;
    private String title;
    private String author;
    private String yearPub;
    private String classify;
    private byte[] img;

    public Books(int ID, String title, String author, String yearPub, String classify, byte[] img) {
        this.ID = ID;
        this.title = title;
        this.author = author;
        this.yearPub = yearPub;
        this.classify = classify;
        this.img = img;
    }

    public int getID(){
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

    public String getClassify() {
        return classify;
    }

    public byte[] getImg() {
        return img;
    }
}
