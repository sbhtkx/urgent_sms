package com.example.mac.urgent_sms;

/**
 * Created by Mac on 24/09/2018.
 */

public class Date {

    private int year, month, day_of_month, hour, minute;
    private boolean isOn;

    public Date(int year, int month, int day_of_month, int hour, int minute){
        this.year = year;
        this.month = month;
        this.day_of_month = day_of_month;
        this.hour = hour;
        this.minute = minute;
        isOn = true;

    }

    public void setIsOn(boolean isOn){
        this.isOn = isOn;
    }

    public int getYear(){
        return year;
    }

    public int getMonth(){
        return month;
    }

    public int getDayOfMonth(){
        return day_of_month;
    }


    public int getHour(){
        return hour;
    }

    public int getMinute(){
        return minute;
    }

    public boolean getIsOn(){
        return isOn;
    }

    public void setYear(int year){
        this.year = year;
    }

    public void setMonth(int month){
        this.month = month;
    }

    public void setDay(int day_of_month){
        this.day_of_month = day_of_month;
    }

    public void setHour(int hour){
        this.hour = hour;
    }

    public void setMinute(int minute){
        this.minute = minute;
    }


}
