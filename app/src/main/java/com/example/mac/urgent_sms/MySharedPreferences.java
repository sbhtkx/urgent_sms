package com.example.mac.urgent_sms;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

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
        editor.putBoolean("switch state",state);
        editor.apply();
    }

    public boolean getSwitchState(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean state = prefs.getBoolean("switch state",false);
        return state;
    }


    public void setContactList(ArrayList<Contact> urgent_contacts, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor  = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(urgent_contacts);
        editor.putString("prefs_urgent_contacts",json);
        editor.apply();
    }

    public ArrayList<Contact> getContactList(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString("prefs_urgent_contacts",null);
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
        editor.putString("prefs_urgent_words",json);
        editor.apply();
    }

    public ArrayList<Word> getUrgentWordsList(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString("prefs_urgent_words",null);
        Type type = new TypeToken<ArrayList<Word>>(){}.getType();
        if(gson.fromJson(json, type) == null){
            return new ArrayList<Word>();
        }
        else{
            return gson.fromJson(json, type);

        }
    }


    public void setContactsState(boolean enable, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor  = prefs.edit();
        editor.putBoolean("prefs_enable_contacts",enable);
        editor.apply();
    }

    public boolean getContactsState(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean state = prefs.getBoolean("prefs_enable_contacts",false);
        return state;
    }

    public void setWordsState(boolean enable, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor  = prefs.edit();
        editor.putBoolean("prefs_enable_words",enable);
        editor.apply();
    }

    public boolean getWordsState(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean state = prefs.getBoolean("prefs_enable words",false);
        return state;
    }

    public void setAutoReplyState(boolean enable, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor  = prefs.edit();
        editor.putBoolean("prefs_enable_auto_reply",enable);
        editor.apply();
    }

    public boolean getAutoReplyState(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean state = prefs.getBoolean("prefs_enable_auto_reply",false);
        return state;
    }

    public void setAutoReplyList(ArrayList<String> auto_reply, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor  = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(auto_reply);
        editor.putString("prefs_auto_reply_list",json);
        editor.apply();
    }

    public ArrayList<String> getAutoReplyList(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString("prefs_auto_reply_list",null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        if(gson.fromJson(json, type) == null){
            ArrayList<String> temp = new ArrayList<String>();
            temp.add(context.getResources().getString(R.string.automatic_reply_1));
            temp.add(context.getResources().getString(R.string.automatic_reply_2));
            temp.add(context.getResources().getString(R.string.automatic_reply_3));
            temp.add(context.getResources().getString(R.string.automatic_reply_4));
            setAutoReplyList(temp,context);
            return temp;
        }
        else{
            return gson.fromJson(json, type);

        }
    }

    public void setAutoReply(String auto_reply, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor  = prefs.edit();
        editor.putString("prefs_auto_reply",auto_reply);
        editor.apply();
    }

    public String getAutoReply(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("prefs_auto_reply",null);

    }

    public void setTimerState(boolean enable, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor  = prefs.edit();
        editor.putString("prefs_enable_timer",""+enable);
        editor.apply();
    }

    public boolean getTimerState(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean state = prefs.getBoolean("prefs_enable_timer",false);
        return state;
    }


    public void setTimerList(ArrayList<Date> dates, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor  = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(dates);
        editor.putString("prefs_timer_list",json);
        editor.apply();
    }

    public ArrayList<Date> getTimerList(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString("prefs_timer_list",null);
        Type type = new TypeToken<ArrayList<Date>>(){}.getType();
        if(gson.fromJson(json, type) == null){
            return new ArrayList<Date>();

        }
        else{
            return gson.fromJson(json, type);

        }
    }

    public boolean getRingtoneState(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean state = prefs.getBoolean("prefs_enable_notification_sound",false);
        return state;
    }


    public String getRingtoneLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String location = prefs.getString("prefs_notification_sound",null);
        return location;
    }


    public void setHasTimerEnableApp(boolean hasPerm, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor  = prefs.edit();
        editor.putBoolean("has app restarted",hasPerm);
        editor.apply();
    }

    public boolean getHasTimerEnableApp(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean state = prefs.getBoolean("has app restarted",false);
        return state;
    }

    public void setPriorMsgTime(Calendar calendar, Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor  = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(calendar);
        editor.putString("prior msg time",json);
        editor.apply();
    }

    public Calendar getPriorMsgTime(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString("prior msg time",null);
        Type type = new TypeToken<Calendar>(){}.getType();
        return gson.fromJson(json, type);

    }



}
