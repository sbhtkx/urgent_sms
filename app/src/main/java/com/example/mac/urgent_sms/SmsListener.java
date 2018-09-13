package com.example.mac.urgent_sms;

/**
 * Created by Mac on 26/08/2018.
 */

public interface SmsListener {
    public void messageReceived(String messageText, String sender);
}
