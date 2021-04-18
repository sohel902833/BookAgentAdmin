package com.sohel.bookagentadmin.LocalDatabase;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class UserShared {
    Activity activity;

    public UserShared(Activity activity) {
        this.activity = activity;
    }
    public void saveUserData(String phone,String password,String userType,String uID){
        SharedPreferences sharedPreferences=activity.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("phone",phone);
        editor.putString("password",password);
        editor.putString("userType",userType);
        editor.putString("uID",uID);
        editor.commit();
    }
    public void logoutUser(){
        SharedPreferences sharedPreferences=activity.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("userType","");
        editor.commit();
    }
    public String getUserType(){
        SharedPreferences sharedPreferences=activity.getSharedPreferences("user",Context.MODE_PRIVATE);
        String userType=sharedPreferences.getString("userType","");
        return  userType;
    }
    public String getUID(){
        SharedPreferences sharedPreferences=activity.getSharedPreferences("user",Context.MODE_PRIVATE);
        String uID=sharedPreferences.getString("uID","");
        return  uID;
    }
    public String getUserPhone(){
        SharedPreferences sharedPreferences=activity.getSharedPreferences("user",Context.MODE_PRIVATE);
        String phone=sharedPreferences.getString("phone","");
        return  phone;
    }
    public String getUserPassword(){
        SharedPreferences sharedPreferences=activity.getSharedPreferences("user",Context.MODE_PRIVATE);
        String password=sharedPreferences.getString("password","");
        return  password;
    }


}
