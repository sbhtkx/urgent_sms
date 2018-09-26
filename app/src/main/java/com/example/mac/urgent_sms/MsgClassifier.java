package com.example.mac.urgent_sms;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;



public class MsgClassifier {

    private WordsManager wm;
    private DataManager dm;
    private double[][] w1;
    private double[][] w2;
    private double[][] b1;
    private double b2;
    private double threshold;

    private Context ctx;

    private static MsgClassifier instance = null;

    private MsgClassifier(WordsManager wm, DataManager dm, Context forToast){
        this.dm = dm;
        this.wm = wm;
        threshold = 0.5;
        this.ctx = forToast;
        updateWeights();
    }

    public static MsgClassifier getInstance(WordsManager wm, DataManager dm, Context forToast) {
        if (instance == null) {
            instance = new MsgClassifier(wm, dm, forToast);
        }
        return instance;
    }



    public boolean isUrgent(String msg, ArrayList<Contact> contacts,ArrayList<Word> words){
        updateWeights();
        int[] w= wm.stringToVector(msg);
        double[][] x = new double[1][w.length];
        for(int i = 0; i < x[0].length; i++){
            x[0][i] = (double)w[i];
        }
        double[][] s = matMul(x, w1);
        updateWeights();
        double[][] z1 = matAdd(s, b1);
        updateWeights();
        double[][] activation1 = matRelu(z1);
        updateWeights();
        double z2 = matMul(activation1,w2)[0][0] + b2;
        updateWeights();
        double y = 1 / (1 + Math.exp(-z2));

        Log.d("msgc1",Double.toString(y));

        Toast.makeText(ctx, msg+": "+Double.toString(y), Toast.LENGTH_LONG).show();

        return y > threshold;
    }


    public static double[][] matMul(double[][] A, double[][] B){
        int rowsA = A.length;
        int colsA = A[0].length;
        int colsB = B[0].length;
        //new matrix to hold result
        double[][] C = new double [rowsA][colsB];
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

    private static double[][] matAdd(double[][] A, double[][] B){
        double[][] C = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++)
        {
            for (int j = 0; j < A[i].length; j++)
            {
                C[i][j] = A[i][j] + B[i][j];
            }
        }
        return C;
    }

    private static double[][] matRelu(double[][] A){
        double[][] B = new double[A.length][A[0].length];

        for (int i = 0; i < A.length; i++)
        {
            for (int j = 0; j < A[i].length; j++)
            {
                B[i][j] = Math.max(0,A[i][j]);
            }
        }
        return B;
    }

    private void updateWeights(){
        if(w1 == null || b1 == null || w2 == null) {
            try {
                // load data and convert to matrices
                w1 = dm.loadDoubleMatrixFromInternalStorage("w1.data");
                double[] b1Array = dm.loadDoubleArrayFromInternalStorage("b1.data");
                b1 = new double[1][b1Array.length];
                b1[0] = b1Array;
//            double[] w2Array = dm.loadDoubleArrayFromInternalStorage("w2.data");
                w2 = dm.loadDoubleMatrixFromInternalStorage("w2.data");
                b2 = dm.loadDoubleFromInternalStorage("b2.data");
            } catch (Exception e) {
            }
        }
    }

}






