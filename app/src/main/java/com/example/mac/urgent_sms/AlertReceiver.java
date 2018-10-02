package com.example.mac.urgent_sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by Mac on 01/10/2018.
 */

public class AlertReceiver extends BroadcastReceiver {
    private MySharedPreferences sharedPrefs = MySharedPreferences.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean former_state = sharedPrefs.getSwitchState(context);
        MainActivity.enable_switch.setChecked(true);
        sharedPrefs.setSwitchState(true,context);

        //remove timer from timer_list
//        int id = intent.getExtras().getInt("id");
//        ArrayList<Date> dates = sharedPrefs.getTimerList(context);
//        for(Date date : dates){
//            if(date.getId() == id){
//                dates.remove(date);
//            }
//        }
//        sharedPrefs.setTimerList(dates,context);

        //cancel the alarm
    }
}
