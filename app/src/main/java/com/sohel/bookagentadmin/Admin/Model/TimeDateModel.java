package com.sohel.bookagentadmin.Admin.Model;

public class TimeDateModel {

    int year,month,date;

    public TimeDateModel(){}
    public TimeDateModel(int year, int month, int date) {
        this.year = year;
        this.month = month;
        this.date = date;
    }
  public TimeDateModel(int month, int date) {
        this.month = month;
        this.date = date;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
