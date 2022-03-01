package com.example.librarybookfinder;

public class Users{
    private int ID;
    private String fName;
    private String studNum;
    private String course;
    private byte[] img;

    public Users(int ID, String fName, String studNum, String course, byte[] img) {
        this.ID = ID;
        this.fName = fName;
        this.studNum = studNum;
        this.course = course;
        this.img = img;
    }

    public int getID() {
        return ID;
    }

    public String getfName() {
        return fName;
    }

    public String getStudNum() {
        return studNum;
    }

    public String getCourse() {
        return course;
    }

    public byte[] getImg() {
        return img;
    }
}
