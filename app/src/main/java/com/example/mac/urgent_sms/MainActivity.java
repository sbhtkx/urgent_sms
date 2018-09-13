package com.example.mac.urgent_sms;

import android.content.Intent;
import android.support.annotation.NonNull;
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
    Intent intent_registered=new Intent("com.example.mac.urgent_sms.RegisteredActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        daniel
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

        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            //user already signed in
            Log.d("AUTH",auth.getCurrentUser().getEmail());
            Toast.makeText(this,"user!=null",Toast.LENGTH_LONG).show();
            startActivity(intent_registered);
        }
        else {
            Toast.makeText(this,"else",Toast.LENGTH_LONG).show();
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder().setProviders(
                            AuthUI.FACEBOOK_PROVIDER,
                            AuthUI.GOOGLE_PROVIDER).setLogo(R.drawable.main_page)
                    .build(), RC_SIGN_IN);
            Toast.makeText(this,"returned to else stmt",Toast.LENGTH_LONG).show();
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
                Toast.makeText(this,"logged in",Toast.LENGTH_LONG).show();
                startActivity(intent_registered);

            }
            else{
                //user not authenticated
                Toast.makeText(this,"result not ok",Toast.LENGTH_LONG).show();

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
