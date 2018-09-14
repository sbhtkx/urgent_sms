package com.example.mac.urgent_sms;

/**
 * Created by Mac on 14/09/2018.
 */

public interface MyDatabase {


    public String getUserId();
    public void setSwitchState(boolean state);
    public void getSwitchState(MyCallback<String> callback);

}
