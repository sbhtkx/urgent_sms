package com.example.mac.urgent_sms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private static TextView msg_text;
    private static Switch enable_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        msg_text = (TextView) findViewById(R.id.msg_txt);
        enable_switch = (Switch) findViewById(R.id.enable_switch);

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

    }
}
