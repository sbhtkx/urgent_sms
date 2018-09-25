package com.example.mac.urgent_sms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email;
    private EditText subject;
    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        subject = (EditText) findViewById(R.id.subject_txt);
        message = (EditText) findViewById(R.id.message_body_txt);
        Button send_btn = (Button) findViewById(R.id.send_btn);

        send_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if( (subject.getText().length() == 0) || (message.getText().length() == 0) ) {
            if(message.getText().length() == 0){
                Toast.makeText(this, "Message body is empty", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Please fill message subject", Toast.LENGTH_SHORT).show();
            }

        }

        else{
            String to = getResources().getString(R.string.our_mail);
            String sub = subject.getText().toString();
            String message_body = message.getText().toString();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
            intent.putExtra(Intent.EXTRA_SUBJECT,sub);
            intent.putExtra(Intent.EXTRA_TEXT,message_body);

            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent,"Select Email clienthg"));
        }


    }
}
