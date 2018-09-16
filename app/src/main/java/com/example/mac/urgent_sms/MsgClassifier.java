package com.example.mac.urgent_sms;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import android.util.Log;


public class MsgClassifier {

    private WordsManager wm;
    private AssetManager am;
    private float[][] w1;
    private float[][] w2;
    private float[][] b1;
    private float b2;
    private double threshold;

    public MsgClassifier(WordsManager wm, AssetManager am){
        this.wm = wm;
        this.am = am;
        try {
            w1 = loadMatrix("w1.txt");
            b1 = loadMatrix("b1.txt");
            w2 = loadMatrix("w2.txt");
            b2 = loadNumber("b2.txt");
        }catch(Exception e){
        }
        threshold = 0.8;
    }

    public boolean isUrgent(String msg){
        int[] w= wm.stringToVector(msg);
        float[][] x = new float[1][w.length];
        for(int i = 0; i < x[0].length; i++){
            x[0][i] = (float)w[i];
        }
        float[][] s = matMul(x, w1);
        float[][] z1 = matAdd(s, b1);

        float[][] activation1 = matRelu(z1);
        float z2 = matMul(activation1,w2)[0][0] + b2;
        double y = 1 / (1 + Math.exp(-z2));
        return y > threshold;
    }

//    private float[] loadArray(String file_name) throws Exception {
//        BufferedReader br;
//        String line, lines = "";
//        InputStream is = am.open(file_name);
//        br = new BufferedReader(new InputStreamReader(is));
//        while ((line = br.readLine()) != null) {
//            lines += line;
//            if (line.equalsIgnoreCase("quit")) {
//                break;
//            }
//        }
//        StringTokenizer stk = new StringTokenizer(lines, ",\n");
//        float[] array = new float[stk.countTokens()];
//        for (int i = 0; i < array.length && stk.hasMoreElements(); i++) {
//            String s = stk.nextToken();
//            float f = Float.parseFloat(s);
//            array[i] = f;
//        }
//
//        br.close();
//
//        return array;
//    }

    private float[][] loadMatrix(String file_name)throws Exception{
        BufferedReader br;
        String line, lines = "";
        InputStream is = am.open(file_name);
        br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            lines += line+"\n";
        }
        StringTokenizer stkRow = new StringTokenizer(lines,"\n");
        int countRows = stkRow.countTokens();
        StringTokenizer stkCol = new StringTokenizer(stkRow.nextToken(),",");
        float[][] matrice = new float[countRows][stkCol.countTokens()];
        for(int j = 0; j < matrice[0].length && stkCol.hasMoreElements(); j++){
            matrice[0][j] = Float.parseFloat(stkCol.nextToken());
        }
        for(int i = 1; i < matrice.length && stkRow.hasMoreElements(); i++){
            stkCol = new StringTokenizer(stkRow.nextToken(),",");
            for(int j = 0; j < matrice[i].length && stkCol.hasMoreElements(); j++){
                matrice[i][j] = Float.parseFloat(stkCol.nextToken());
            }
        }

        // close the streams using close method
        try {
            br.close();
        }
        catch (IOException ioe) {
        }

        return matrice;
    }

    private float loadNumber(String file_name) throws Exception {
        BufferedReader br;
        String line;
        InputStream is = am.open(file_name);
        br = new BufferedReader(new InputStreamReader(is));
        if ((line = br.readLine()) == null) {
            return 0;
        }
        float ans = Float.parseFloat(line);
        try {
                br.close();
        }
        catch (IOException ioe) {
        }
        return ans;
    }

    public static float[][] matMul(float[][] A, float[][] B){
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;
        //new matrix to hold result
        float[][] C = new float [rowsA][colsB];
        //start across rows of A
        for (int i = 0; i < rowsA; i++) {
            //work across cols of B
            for(int j = 0; j < colsB; j++) {
                //now complete the addition and multiplication
                for(int k = 0; k < colsA; k++) {
                    C[i][j] += A [i][k] * B[k][j];
                }
            }
        }
        return C;
    }

    private static float[][] matAdd(float[][] A, float[][] B){
        float[][] C = new float[A.length][A[0].length];
        for (int i = 0; i < A.length; i++)
        {
            for (int j = 0; j < A[i].length; j++)
            {
                C[i][j] = A[i][j] + B[i][j];
            }
        }
        return C;
    }

    private static float[][] matRelu(float[][] A){
        float[][] B = new float[A.length][A[0].length];

        for (int i = 0; i < A.length; i++)
        {
            for (int j = 0; j < A[i].length; j++)
            {
                B[i][j] = Math.max(0,A[i][j]);
            }
        }
        return B;
    }
}
