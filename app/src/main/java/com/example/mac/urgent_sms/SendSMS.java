package com.example.mac.urgent_sms;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

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
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, msg, null, null);
//            Toast.makeText(context, "Message Sent",
//                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
//            Toast.makeText(context,ex.getMessage().toString(),
//                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

}
