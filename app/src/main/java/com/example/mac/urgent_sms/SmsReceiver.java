package com.example.mac.urgent_sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Mac on 26/08/2018.
 */


public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        String sender = null;
        for(int i=0; i<pdus.length; i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            sender = smsMessage.getOriginatingAddress();
            String messageBody = smsMessage.getMessageBody();

            mListener.messageReceived(messageBody,sender);



        }



    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }


}
