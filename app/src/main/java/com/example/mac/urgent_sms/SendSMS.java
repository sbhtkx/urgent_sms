package com.example.mac.urgent_sms;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;

/**
 * Created by Mac on 22/09/2018.
 */

public class SendSMS {

    String msg;
    String phoneNumber;
    Context context;

    public SendSMS(Context context, String phoneNumber, String msg){
        this.phoneNumber = phoneNumber;
        this.msg = msg;
        this.context = context;
    }

    public void sendMsg(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(phoneNumber));
        intent.putExtra("sms_body", msg);
        context.startActivity(intent);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber,null,msg,null,null);
    }

}
