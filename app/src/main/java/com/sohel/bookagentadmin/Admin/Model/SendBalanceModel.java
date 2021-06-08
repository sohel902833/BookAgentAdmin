package com.sohel.bookagentadmin.Admin.Model;

import com.sohel.bookagentadmin.Admin.SendBalanceActivity;

public class SendBalanceModel {

    String id,from,fromName,to,toName,time,date;
    int amount,agentNew,adminNew;


    public SendBalanceModel(){

    }


    public SendBalanceModel(String id, String from,String fromName, String to,String toName, String time, String date, int amount, int agentNew, int adminNew) {
        this.id = id;
        this.from = from;
        this.fromName=fromName;
        this.to = to;
        this.toName=toName;
        this.time = time;
        this.date = date;
        this.amount = amount;
        this.agentNew = agentNew;
        this.adminNew = adminNew;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAgentNew() {
        return agentNew;
    }

    public void setAgentNew(int agentNew) {
        this.agentNew = agentNew;
    }

    public int getAdminNew() {
        return adminNew;
    }

    public void setAdminNew(int adminNew) {
        this.adminNew = adminNew;
    }
}
