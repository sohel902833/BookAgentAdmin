package com.sohel.bookagentadmin.Admin.Model;

public class BalanceHistory {
    String id;
    String date,time;
    int balance;

    public BalanceHistory(){

    }

    public BalanceHistory(String id,String date, String time, int balance) {
        this.id=id;
        this.date = date;
        this.time = time;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
