package com.example.mac.urgent_sms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 0;
    private FirebaseAuth auth;
    private MySharedPreferences sharedPrefs = MySharedPreferences.getInstance();
    Intent intent_main;
    Intent intent_settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        intent_main = new Intent(this,MainActivity.class);


        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            //user already signed in
            Log.d("AUTH",auth.getCurrentUser().getEmail());
            startActivity(intent_main);
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
                sharedPrefs.setSwitchState(false,this);

                Toast.makeText(this,"You are logged in",Toast.LENGTH_LONG).show();

                startActivity(intent_main);

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
//                        Log.add_contact("AUTH","USER LOGGED OUT!");
//                        finish();
//                    }
//                });
//
//                break;
//
//        }
    }
}
