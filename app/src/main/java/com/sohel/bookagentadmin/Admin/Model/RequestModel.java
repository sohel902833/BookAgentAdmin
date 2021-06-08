package com.sohel.bookagentadmin.Admin.Model;

public class RequestModel {
    String uid,date,time;

    public RequestModel(){

    }
    public RequestModel(String uid, String date, String time) {
        this.uid = uid;
        this.date = date;
        this.time = time;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
