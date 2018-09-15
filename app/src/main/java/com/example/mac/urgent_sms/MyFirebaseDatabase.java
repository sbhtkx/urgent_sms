package com.example.mac.urgent_sms;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        database.child("users").child(getUserId()).child("switch state").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onSuccess(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
