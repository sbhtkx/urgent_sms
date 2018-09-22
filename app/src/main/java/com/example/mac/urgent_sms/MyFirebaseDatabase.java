package com.example.mac.urgent_sms;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Mac on 14/09/2018.
 */

public class MyFirebaseDatabase implements MyDatabase {

    private static MyFirebaseDatabase instance = null;
    private DatabaseReference database;

    private MyFirebaseDatabase(){
        database = FirebaseDatabase.getInstance().getReference();
    }


    public static MyFirebaseDatabase getInstance(){
        if(instance == null){
            instance = new MyFirebaseDatabase();
        }
        return  instance;
    }



    @Override
    public void setName() {
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.child("users").child(uid).child("name").setValue(name);
    }


    @Override
    public String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public String getName() {
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    @Override
    public void setSwitchState(boolean state) {
        database.child("users").child(getUserId()).child("switch state").setValue(state);
    }

    @Override
    public void getSwitchState(final MyCallback<String> callback) {
        database.child("tensorflow").child("versions").child("b1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onSuccess(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getVersion(final MyCallback<String> callback){
        //Log.d("updat5","start");
        database.child("tensorflow").child("versions").child("b1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("updat5",dataSnapshot.getValue().toString());
                callback.onSuccess(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


//    public void bla(){
//        MyCallback mc = new MyCallback() {
//            @Override
//            public void onSuccess(Object data) {
//
//            }
//        };
//        getSwitchState(mc);
//    }


    @Override
    public void setContactList(ArrayList<Contact> contacts) {
        database.child("users").child(getUserId()).child("urgent contacts").setValue(contacts);
    }

    @Override
    public void getContactList(final MyCallback<ArrayList<Contact>> callback) {
        database.child("users").child(getUserId()).child("urgent contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Contact>> t = new GenericTypeIndicator<ArrayList<Contact>>() {};
                if(dataSnapshot.getValue() == null){
                    callback.onSuccess(new ArrayList<Contact>());
                }
                else{
                    callback.onSuccess(dataSnapshot.getValue(t));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void setWordList(ArrayList<String> words) {
        database.child("words").child(getUserId()).child("urgent words").setValue(words);
    }

    @Override
    public void getWordList(final MyCallback<ArrayList<String>> callback) {
        database.child("words").child(getUserId()).child("urgent words").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                if(dataSnapshot.getValue() == null){
                    callback.onSuccess(new ArrayList<String>());
                }
                else{
                    callback.onSuccess(dataSnapshot.getValue(t));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
