package com.example.mac.urgent_sms;

import android.content.res.AssetManager;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class WordsManager {

    private String[] voc;
    private StringTokenizer stk;

    public WordsManager(AssetManager am){
        // initialize the voc (load from file)
        String text = "";
        try {
            InputStream is = am.open("voc.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);

        }catch(IOException ex){
            ex.printStackTrace();
        }
        stk = new StringTokenizer(text,"\n");
        voc = new String[stk.countTokens()];
        for(int i = 0; i<voc.length; i++){
            voc[i] = stk.nextToken().replace("\n","").replace("\r","");
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
        for (String s:voc) {
            Log.d("wwword", s);
            if(s.contains("what"))Log.d("wwword1","@"+s+"@");
        }
        for(int i = 0; i<voc.length; i++){
            Log.d("wvoc", voc[i]);
            if(voc[i].equals("what")){
                Log.d("whaaat","0");
                if(words.contains(voc[i])) Log.d("whaaat","1");
            }
            if(words.contains(voc[i])){
                vector[i] = 1; // vector[i] + 1
            }
            else{
                vector[i] = 0;
            }
        }
        Log.d("s2v",str+": "+Arrays.toString(vector));
        return vector;
    }
}
