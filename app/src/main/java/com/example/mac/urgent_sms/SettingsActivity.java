package com.example.mac.urgent_sms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView msg_text;
    private Switch enable_switch;
    private Button urgentContact_btn;
    private Button urgentWords_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        msg_text = (TextView) findViewById(R.id.msg_txt);
        enable_switch = (Switch) findViewById(R.id.enable_switch);
        urgentContact_btn = (Button) findViewById(R.id.setContacts_btn);
        urgentWords_btn = (Button) findViewById(R.id.setWords_btn);

        enable_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton button, boolean isChecked){
                if(isChecked){
                    SmsReceiver.bindListener(new SmsListener() {
                        @Override
                        public void messageReceived(String messageText) {

                            msg_text.setText(messageText);


                        }
                    });
                }

            }

        });

        urgentWords_btn.setOnClickListener(this);
        urgentContact_btn.setOnClickListener(this);

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
