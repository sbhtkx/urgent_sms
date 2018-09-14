package com.example.mac.urgent_sms;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 0;
    private FirebaseAuth auth;
    private MyDatabase my_database = MyFirebaseDatabase.getInstance();
    Intent intent_registered;
    Intent settings_intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent_registered = new Intent("com.example.mac.urgent_sms.RegisteredActivity");
        settings_intent = new Intent(this,SettingsActivity.class);

        WordsManager wm;
        try {
            wm = new WordsManager(getAssets());
            MsgClassifier mc = new MsgClassifier(wm, getAssets());
            boolean police = mc.isUrgent("asap the police is here!!!");
            boolean eat = mc.isUrgent("what is planned to eat today?");
            Log.d("urgency","police"+ String.valueOf(police));
            Log.d("urgency","eat"+ String.valueOf(eat));
        }
        catch(Exception e){

        }

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            //user already signed in
            Log.d("AUTH",auth.getCurrentUser().getEmail());
            startActivity(intent_registered);
        }
        else {
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder().setProviders(
                            AuthUI.FACEBOOK_PROVIDER,
                            AuthUI.GOOGLE_PROVIDER).setLogo(R.drawable.main_page)
                    .build(), RC_SIGN_IN);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                //user logged in
                Toast.makeText(this,"result ok",Toast.LENGTH_LONG).show();
                Log.d("AUTH",auth.getCurrentUser().getEmail());
                my_database.setSwitchState(false);
                Toast.makeText(this,"logged in",Toast.LENGTH_LONG).show();
                startActivity(intent_registered);

            }
            else{
                //user not authenticated
                Log.d("AUTH","NOT AUTHENTICATED");
            }
        }
    }




    @Override
    public void onClick(View view) {
//        switch(view.getId()){
//            case(R.id.logout_btn):
//                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.d("AUTH","USER LOGGED OUT!");
//                        finish();
//                    }
//                });
//
//                break;
//
//        }
    }
}
