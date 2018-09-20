package com.example.mac.urgent_sms;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;



public class DataManager {

    private DatabaseReference weightsRef, versionsRef, vocabularyRef, database;
    private Context ctx;
    private Map<String,Boolean> needToUpdate;


    public DataManager(Context ctx){
        database = FirebaseDatabase.getInstance().getReference();
        weightsRef = database.child("tensorflow_data").child("wheights").child("w1");
        this.ctx = ctx;
    }


    public void fetchData() {
        final MyCallback<String> callback = new MyCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Log.d("great2",data);
                writeToInternalStorageFile(ctx, "w1", data);

            }

        };
        weightsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("great1", dataSnapshot.getValue().toString());
//                writeToInternalStorageFile(ctx, "file", dataSnapshot.getValue().toString());

//                for(String s: dataSnapshot.getValue()){


                callback.onSuccess(dataSnapshot.getValue().toString());
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void writeToInternalStorageFile(Context ctx, String fileName, String content){
        try {
            FileOutputStream fileOutputStream = ctx.openFileOutput(fileName, MODE_PRIVATE);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
        }catch(IOException e){

        }
    }

    public String readFromInternalStorageFile(Context ctx, String fileName){
        try {
            String content;
            FileInputStream fileInputStream = ctx.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            while((content=bufferedReader.readLine())!=null){
                stringBuffer.append(content + "\n");
            }
            return stringBuffer.toString();
        }catch(IOException e){

        }
        return null;
    }

    public double[][] loadMatrix(String fileName){

        String content = readFromInternalStorageFile(ctx, fileName);
        content = content.replaceAll("\\s+", "");  // delete blank spaces

        Log.d("st10", content.substring(0,200));
        Log.d("st11",content.substring(content.length()-200));

        StringTokenizer stkRow = new StringTokenizer(content,"]");
        int countRows = stkRow.countTokens();
        StringTokenizer stkCol = new StringTokenizer(stkRow.nextToken(),"[],");
        double[][] matrix = new double[countRows][stkCol.countTokens()];
        // first row
        for(int j = 0; j < matrix[0].length && stkCol.hasMoreElements(); j++){
            matrix[0][j] = Double.parseDouble(stkCol.nextToken());
        }
        // rest of rows
        for(int i = 1; i < matrix.length && stkRow.hasMoreElements(); i++){
            stkCol = new StringTokenizer(stkRow.nextToken(),",");
            for(int j = 0; j < matrix[i].length && stkCol.hasMoreElements(); j++){
                String tmp = stkCol.nextToken();
                tmp = tmp.replaceAll("\\[","");
                Log.d("st2",tmp);
                matrix[i][j] = Double.parseDouble(tmp);
            }
        }
        Log.d("st2","FIN");
        return matrix;
    }

    public double[] loadArray(String fileName){

        String content = readFromInternalStorageFile(ctx, fileName);
        content = content.replaceAll("\\s+", "");  // delete blank spaces

        StringTokenizer stk = new StringTokenizer(content,"[],");

        double[] array = new double[stk.countTokens()];

        for(int i = 0; i < array.length && stk.hasMoreElements(); i++){
            array[i] = Double.parseDouble(stk.nextToken());
        }

        return array;

    }

    public double loadNumber(String fileName){
        String content = readFromInternalStorageFile(ctx, fileName);
        double number = Double.parseDouble(content);
        return number;
    }

}
