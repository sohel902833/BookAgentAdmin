package com.sohel.bookagentadmin.Admin.Model;

public class Users {

    private String uid;
    private String image;
    private String name;
    private String email;
    private String passwrord;
    private  String time,date;
    private String phone;
    public Users()
    {

    }

    public Users(String uid,String image, String name, String email,String phone, String passwrord) {
       this.uid=uid;
        this.image = image;
        this.name = name;
        this.email = email;
        this.passwrord = passwrord;
        this.phone=phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswrord() {
        return passwrord;
    }

    public void setPasswrord(String passwrord) {
        this.passwrord = passwrord;
    }
}
