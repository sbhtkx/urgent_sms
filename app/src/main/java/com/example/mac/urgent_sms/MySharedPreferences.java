package com.example.mac.urgent_sms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Mac on 17/09/2018.
 */

public class MySharedPreferences {


    public static MySharedPreferences instance = null;
    private MySharedPreferences(){

    }

    public static MySharedPreferences getInstance(){
        if(instance == null){
            instance = new MySharedPreferences();
        }
        return instance;
    }


    public void setSwitchState(boolean state, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor  = prefs.edit();
        editor.putString("switch state",""+state);
        editor.apply();
    }

    public boolean getSwitchState(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String state = prefs.getString("switch state",null);
        if(state.equals("true")){
            return true;
        }
        else{
            return false;
        }
    }


    public void setContactList(ArrayList<Contact> urgent_contacts, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor  = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(urgent_contacts);
        editor.putString("urgent contacts",json);
        editor.apply();
    }

    public ArrayList<Contact> getContactList(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString("urgent contacts",null);
        Type type = new TypeToken<ArrayList<Contact>>(){}.getType();
        if(gson.fromJson(json, type) == null){
            return new ArrayList<Contact>();
        }
        else{
            return gson.fromJson(json, type);

        }
    }


    public void setUrgentWordsList(ArrayList<Word> urgent_words, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor  = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(urgent_words);
        editor.putString("urgent words",json);
        editor.apply();
    }

    public ArrayList<Word> getUrgentWordsList(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString("urgent words",null);
        Type type = new TypeToken<ArrayList<Word>>(){}.getType();
        if(gson.fromJson(json, type) == null){
            return new ArrayList<Word>();
        }
        else{
            return gson.fromJson(json, type);

        }
    }

}
