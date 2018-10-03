package com.example.mac.urgent_sms;

import java.util.Calendar;

/**
 * Created by Mac on 24/09/2018.
 */

public class Date {

    private boolean isOn;
    private int id;
    private Calendar calendar;
    private static int counter = 0;

    public Date(Calendar calendar){
        this.calendar = calendar;
        isOn = true;
        id = ++counter;
        calendar.set(Calendar.SECOND,0);

    }

    public void setIsOn(boolean isOn){
        this.isOn = isOn;
    }

    public int getYear(){
        return calendar.get(Calendar.YEAR);
    }

    public int getMonth(){
        return calendar.get(Calendar.MONTH);
    }

    public int getDayOfMonth(){
        return calendar.get(Calendar.DAY_OF_MONTH);
    }


    public int getHour(){
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute(){
        return calendar.get(Calendar.MINUTE);
    }

    public boolean getIsOn(){
        return isOn;
    }

    public void setYear(int year){
        calendar.set(Calendar.YEAR,year);
    }

    public void setMonth(int month){
        calendar.set(Calendar.MONTH,month);
    }

    public void setDay(int day_of_month){
        calendar.set(Calendar.DAY_OF_MONTH,day_of_month);    }

    public void setHour(int hour){
        calendar.set(Calendar.HOUR_OF_DAY,hour);    }

    public void setMinute(int minute){
        calendar.set(Calendar.MINUTE,minute);
    }

    public int getId(){
        return id;
    }

    public Calendar getCalendar(){
        return calendar;
    }




}
