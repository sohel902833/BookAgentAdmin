package com.sohel.bookagentadmin.Agent.Model;

public class MessageModel {
    String date;
    String from;
    String message;
    String messageId;
    String time;
    String to;
    String type;


    public MessageModel(){

    }


    public MessageModel(String date, String from, String message, String messageId, String time, String to, String type) {
        this.date = date;
        this.from = from;
        this.message = message;
        this.messageId = messageId;
        this.time = time;
        this.to = to;
        this.type = type;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
