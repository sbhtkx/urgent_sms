package com.example.mac.urgent_sms;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;

public class DataManager {

    private static DataManager instance = null;

    private DatabaseReference databaseRef;
    private Context ctx;
    private Map<String,String> currentVersions, newVersions;
    private Set<String> dataNames;  // the names of the data in firebase: "w1", "vocabulary" etc.
    private MyDatabase fb;


    private DataManager(Context ctx){
        dataNames = new HashSet<>(Arrays.asList("b1","b2","w1","w2","vocabulary"));
        databaseRef = FirebaseDatabase.getInstance().getReference();
        this.ctx = ctx;
        fb = MyFirebaseDatabase.getInstance();

        // load file versions from internal storage
        currentVersions = new HashMap<>();
        newVersions = new HashMap<>();
        for(String name : dataNames) {
            String fileName = name+".version";
            try {
                double version = loadDoubleFromInternalStorage(fileName);
                currentVersions.put(name,Double.toString(version));
            }catch(IOException e){
                writeToInternalStorageFile(ctx, fileName,"0");
                currentVersions.put(name,Double.toString(0));
            }finally {
                newVersions.put(name,Double.toString(0));
            }
        }

        checkVersionsAndUpdateFiles();
    }

    public static DataManager getInstance(Context ctx) {
        if (instance == null){
            instance = new DataManager(ctx);
        }
        return instance;
    }

    private void checkVersionsAndUpdateFiles(){
        // get new versions from firebase

        fb.getVersions( new MyCallback<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot data) {
                for(String name : dataNames){
                    newVersions.put(name,data.child(name).getValue().toString());
                }
                // check which data need to be updated and update
                for (final String name : dataNames) {
                    if(!currentVersions.get(name).equals(newVersions.get(name))){
                        fb.getWeightByName(name, new MyCallback<String>() {
                                    @Override
                                    public void onSuccess(String data) {
                                        writeToInternalStorageFile(ctx, name+".data", data);
                                        writeToInternalStorageFile(ctx,name+".version",newVersions.get(name));
                                    }
                                }
                        );
                    }
                }
            }
        });
    }


    private void writeToInternalStorageFile(Context ctx, String fileName, String content){
        try {
            FileOutputStream fileOutputStream = ctx.openFileOutput(fileName, MODE_PRIVATE);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
        }catch(IOException e){

        }
    }


    // throws IOException if file doesn't exist
    private String readFromInternalStorageFile(Context ctx, String fileName) throws IOException{
        String content;
        FileInputStream fileInputStream = ctx.openFileInput(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuffer stringBuffer = new StringBuffer();
        while((content=bufferedReader.readLine())!=null){
            stringBuffer.append(content + "\n");
        }
        return stringBuffer.toString();
    }

    // throws IOException if file doesn't exist
    public double[][] loadDoubleMatrixFromInternalStorage(String fileName) throws IOException{

        String content = readFromInternalStorageFile(ctx, fileName);
        content = content.replaceAll("\\s+", "");  // delete blank spaces

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
                matrix[i][j] = Double.parseDouble(tmp);
            }
        }
        return matrix;
    }

    // throws IOException if file doesn't exist
    public double[] loadDoubleArrayFromInternalStorage(String fileName) throws IOException{

        String content = readFromInternalStorageFile(ctx, fileName);
        content = content.replaceAll("\\s+", "");  // delete blank spaces

        StringTokenizer stk = new StringTokenizer(content,"[],");

        double[] array = new double[stk.countTokens()];

        for(int i = 0; i < array.length && stk.hasMoreElements(); i++){
            array[i] = Double.parseDouble(stk.nextToken());
        }

        return array;

    }

    public String[] loadStringArrayFromInternalStorage(String fileName) throws IOException{

        String content = readFromInternalStorageFile(ctx, fileName);
        content = content.replaceAll("\\s+", "");  // delete blank spaces

        StringTokenizer stk = new StringTokenizer(content,"[],");

        String[] array = new String[stk.countTokens()];

        for(int i = 0; i < array.length && stk.hasMoreElements(); i++){
            array[i] = stk.nextToken();
        }

        return array;

    }

    // throws IOException if file doesn't exist
    public double loadDoubleFromInternalStorage(String fileName)throws IOException{
        String content = readFromInternalStorageFile(ctx, fileName);
        double number = Double.parseDouble(content);
        return number;
    }

}
