package com.example.mac.urgent_sms;

/**
 * Created by Mac on 16/09/2018.
 */

public class Contact {

    private String name;
    private String phone_number;
    private String img_address;

    private Contact(){

    }

    public Contact(String name, String phone_number){
        this.name = name;
        this.phone_number = phone_number;
    }

    public String getName(){
        return name;
    }

    public String getPhoneNumber(){
        return phone_number;
    }

    public String getImageAddress(){
        return img_address;
    }
}
