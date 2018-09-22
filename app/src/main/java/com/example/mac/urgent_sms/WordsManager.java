package com.example.mac.urgent_sms;

import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class WordsManager {

    private String[] voc;
    private StringTokenizer stk;

    public WordsManager(DataManager dm){
        try {
            voc = dm.loadStringArrayFromInternalStorage("vocabulary.data");
            Log.d("voc111", Arrays.toString(voc));
        }catch(Exception e){
        }
    }

    public int[] stringToVector(String str){
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
}
