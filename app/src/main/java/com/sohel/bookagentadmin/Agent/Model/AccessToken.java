package com.sohel.bookagentadmin.Agent.Model;

public class AccessToken {
    String agentId;
    String token;
    String userId;
    String userName;
    String agentName;
    String date;
    String time;
    boolean sealed;

    public AccessToken(){

    }

    public AccessToken(String agentId, String token, String userId, String userName, String agentName, String date, String time, boolean sealed) {
        this.agentId = agentId;
        this.token = token;
        this.userId = userId;
        this.userName = userName;
        this.agentName = agentName;
        this.date = date;
        this.time = time;
        this.sealed = sealed;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isSealed() {
        return sealed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSealed(boolean sealed) {
        this.sealed = sealed;
    }
}
