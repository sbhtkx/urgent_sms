package com.example.mac.urgent_sms;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class WordsManager {

    private String[] voc;
    private StringTokenizer stk;
    private static WordsManager instance = null;
    private DataManager dm;

    private WordsManager(DataManager dm){
        this.dm = dm;
        updateVoc();
    }

    public static WordsManager getInstance(DataManager dm) {
        if (instance == null) {
            instance = new WordsManager(dm);
        }
        return instance;
    }

    private void updateVoc(){
        if (voc == null) {
            try {
                voc = dm.loadStringArrayFromInternalStorage("vocabulary.data");
            } catch (Exception e) {
                Log.d("voc111", "error");
            }
        }

    }

    public int[] stringToVector(String str){
        updateVoc();

        str = str.replaceAll("[^A-Za-z0-9]"," ").toLowerCase();
        int[] vector = new int[voc.length];
        Set<String> words = new HashSet<>();
        stk = new StringTokenizer(str);
        while(stk.hasMoreElements()){
            String s = stk.nextToken();
            words.add(s);
        }
        for(int i = 0; i<voc.length; i++){
            if(words.contains(voc[i])){
                vector[i] = 1; // vector[i] + 1
            }
            else{
                vector[i] = 0;
            }
        }
        return vector;
    }

    public ArrayList<String> stringToArray(String msg){
        msg = msg.replaceAll("[^A-Za-z0-9]"," ").toLowerCase();
        ArrayList<String> words = new ArrayList<String>();
        stk = new StringTokenizer(msg);
        while(stk.hasMoreElements()){
            String s = stk.nextToken();
            words.add(s);
        }
        return words;
    }
}
