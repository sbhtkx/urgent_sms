package com.example.mac.urgent_sms;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RegisteredActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView welcome;
    private FirebaseAuth auth;
    private Button logout_btn;
    private Button settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        auth = FirebaseAuth.getInstance();
        welcome = (TextView) findViewById(R.id.welcome_txt);
        String name= auth.getCurrentUser().getDisplayName();
        welcome.setText("Hello                                                                                " +name+",");
        logout_btn= (Button) findViewById(R.id.logout_btn);
        settings = (Button) findViewById(R.id.settings_btn);
        logout_btn.setOnClickListener(this);
        welcome.setOnClickListener(this);
        settings.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case(R.id.logout_btn):
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //user is now signed out
                        finish();
                    }
                });
                Toast.makeText(this,"signed-out",Toast.LENGTH_LONG).show();
                Intent main_intent= new Intent("com.example.mac.urgent_sms.MainActivity");
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    startActivity(main_intent);
                    Toast.makeText(this,"user==null",Toast.LENGTH_LONG).show();

                }

                break;

            case (R.id.welcome_txt):
                Intent myAccount_intent = new Intent("com.example.mac.urgent_sms.MyAccountActivity");
                startActivity(myAccount_intent);
                break;

            case (R.id.settings_btn):
                Intent settings_intent = new Intent("com.example.mac.urgent_sms.SettingsActivity");
                startActivity(settings_intent);
                break;



        }
    }
}
