package com.example.mac.urgent_sms;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

/**
 * Created by Mac on 14/09/2018.
 */

public interface MyDatabase {


    public String getUserId();
    public void setSwitchState(boolean state);
    public void getSwitchState(MyCallback<String> callback);
    public String getName();
    public void setName();
    public void setContactList(ArrayList<Contact> contacts);
    public void getContactList(MyCallback<ArrayList<Contact>> callback);

    public void setWordList(ArrayList<String> words);
    public void getWordList(MyCallback<ArrayList<String>> callback);

    public void getVersions(MyCallback<DataSnapshot> callback);
    public void getWeightByName(String name, MyCallback<String> myCallback);

}
