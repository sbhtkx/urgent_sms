package com.example.mac.urgent_sms;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;


public class MsgClassifier {

    private WordsManager wm;
    private DataManager dm;
    private double[][] w1;
    private double[][] w2;
    private double[][] b1;
    private double b2;
    private double threshold;

    private final static double WORD_URG_LEVEL_1 = 0.1;
    private final static double WORD_URG_LEVEL_2 = 0.3;
    private final static double WORD_URG_LEVEL_3 = 0.5;

    private final static double CONTACT_URG_LEVEL_1 = 0.1;
    private final static double CONTACT_URG_LEVEL_2 = 0.3;
    private final static double CONTACT_URG_LEVEL_3 = 0.5;
    private final static double CONTACT_ALWAYS_URGENT = 1;


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

        double urgency_addition = 0;
        if(words != null){
            urgency_addition = urgency_addition + urgWordsAddition(wm.stringToArray(msg),words);
        }
        if(contacts != null){
            urgency_addition = urgency_addition + urgContactsAddition(contacts);
        }

        y = y + urgency_addition; //decide how to calc this

        Log.d("msgc1",Double.toString(y));

        Toast.makeText(ctx, msg+": "+Double.toString(y), Toast.LENGTH_LONG).show();

        return y > threshold;
    }

    private double urgWordsAddition(ArrayList<String> msg_arr, ArrayList<Word> words){
        double urg_add = 0;
        for(int i=0; i<words.size(); i++){
            for(int j=0; j<msg_arr.size(); j++){
                if(words.get(i).getWord().equals(msg_arr.get(j))){
                    switch(words.get(i).getUrgencyLevel()){
                        case(1):
                            urg_add = calcWordUrgLevel(urg_add,WORD_URG_LEVEL_1);
                            break;

                        case(2):
                            urg_add = calcWordUrgLevel(urg_add,WORD_URG_LEVEL_2);
                            break;

                        case(3):
                            urg_add = calcWordUrgLevel(urg_add,WORD_URG_LEVEL_3);
                            break;

                    }
                    break; //TO CHECK!!!
                }
            }

        }
        return urg_add;
    }

    private double urgContactsAddition(ArrayList<Contact> contacts){
        double urg_add = 0;
        for(Contact contact : contacts){
            switch(contact.getUrgencyLevel()){
                case(1):
                    urg_add = calcContactUrgLevel(urg_add,CONTACT_URG_LEVEL_1);
                    break;

                case(2):
                    urg_add = calcContactUrgLevel(urg_add,CONTACT_URG_LEVEL_2);
                    break;

                case(3):
                    urg_add = calcContactUrgLevel(urg_add,CONTACT_URG_LEVEL_3);
                    break;

                case(4):
                    urg_add = calcContactUrgLevel(urg_add,CONTACT_ALWAYS_URGENT);
                    break;

            }
        }
        return urg_add;
    }

    private double calcWordUrgLevel(double sum, double level_addition){

        return 0;
    }

    private double calcContactUrgLevel(double sum, double leve_addition){

        return 0;
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






