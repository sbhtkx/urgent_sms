package com.example.mac.urgent_sms;

/**
 * Created by Mac on 17/09/2018.
 */

public class Word {

    private String word;
    private int urgency_level;

    public Word(String word, int urgency_level){
        this.word = word.toLowerCase();
        this.urgency_level = urgency_level;
    }

    public String getWord(){
        return word;
    }

    public int getUrgencyLevel(){
        return urgency_level;
    }

    public void setWord(String word){
        this.word = word;
    }

    public void setUrgency_level(int urgency_level){
        this.urgency_level = urgency_level;
    }





}
