package com.example.mac.urgent_sms;

import android.*;
import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;

/**
 * Created by Mac on 22/09/2018.
 */

public class SendSMS {

    String msg;
    String phoneNumber;

    public SendSMS(String phoneNumber, String msg){
        this.phoneNumber = phoneNumber;
        this.msg = msg;

    }

    public void sendMsg(){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber,null,msg,null,null);
    }

}
