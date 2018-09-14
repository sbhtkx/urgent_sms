package com.example.mac.urgent_sms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity2 extends AppCompatActivity implements View.OnClickListener{

    private TextView msg_text;
    private TextView sender_txt;
    private Switch enable_switch;
    private Button urgentContact_btn;
    private Button urgentWords_btn;
    private MyDatabase my_database = MyFirebaseDatabase.getInstance();
    private static boolean isEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);

        msg_text = (TextView) findViewById(R.id.msg_txt);
        sender_txt = (TextView) findViewById(R.id.sender_txt);
        enable_switch = (Switch) findViewById(R.id.enable_switch);
        urgentContact_btn = (Button) findViewById(R.id.setContacts_btn);
        urgentWords_btn = (Button) findViewById(R.id.setWords_btn);

        enable_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton button, boolean isChecked){
                if(isChecked){
                    my_database.setSwitchState(true);
                    SmsReceiver.bindListener(new SmsListener() {
                        @Override
                        public void messageReceived(String messageText, String sender) {

                            msg_text.setText(messageText);
                            sender_txt.setText(sender);

                        }
                    });
                }
                else{
                    my_database.setSwitchState(false);
                }

            }

        });



        urgentWords_btn.setOnClickListener(this);
        urgentContact_btn.setOnClickListener(this);

    }


    protected void onStart() {
        super.onStart();

        my_database.getSwitchState(new MyCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if(data.equals("true")){
                    Log.d("yael",data);
                    enable_switch.setChecked(true);
                }
                else{
                    Log.d("yael",data);

                    enable_switch.setChecked(false);
                }

            }
        });



    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case(R.id.setContacts_btn):
                Intent urgentContacts_intent = new Intent(this, UrgentContactsActivity.class);
                startActivity(urgentContacts_intent);
                break;


            case(R.id.setWords_btn):
                Intent urgentWords_intent = new Intent(this, UrgentWordsActivity.class);
                startActivity(urgentWords_intent);
                break;
        }
    }
}
