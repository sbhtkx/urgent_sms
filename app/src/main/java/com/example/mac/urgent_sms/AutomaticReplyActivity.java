package com.example.mac.urgent_sms;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AutomaticReplyActivity extends AppCompatActivity implements View.OnClickListener{

    private Dialog add_reply_dialog;
    private MySharedPreferences sharedPrefs = MySharedPreferences.getInstance();
    private ArrayList<String> autoReplyList;
    private ImageView rmv_reply;
    private static boolean isEdit = false;
    private TextView msg_rmv_reply;
    private RelativeLayout layout;
    private RadioGroup radioGroup;
    private ImageView add_reply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatic_reply);

        layout = (RelativeLayout) findViewById(R.id.auto_reply_layout);

        add_reply = (ImageView) findViewById(R.id.add_reply);
        rmv_reply = (ImageView) findViewById(R.id.rmv_reply);
        msg_rmv_reply = (TextView) findViewById(R.id.msg_rmv_reply);
        Button ok_btn = (Button) findViewById(R.id.ok_btn_autoReply);

        autoReplyList = sharedPrefs.getAutoReplyList(this);

        showRadioGroup();


        add_reply_dialog = new Dialog(this);

        add_reply.setOnClickListener(this);
        rmv_reply.setOnClickListener(this);
        ok_btn.setOnClickListener(this);


    }


    private void showRadioGroup(){
        layout.removeView(radioGroup);
        radioGroup = new RadioGroup(this);

        //set margins of radioGroup
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(10,130,40,30);
        radioGroup.setLayoutParams(param);

        //add radioButtons into radioGroup
        RadioGroup.LayoutParams layoutParams;
        for(int i=0; i<autoReplyList.size(); i++){
            RadioButton radioBtn = new RadioButton(this);
            radioBtn.setText(autoReplyList.get(i));
            radioBtn.setTextColor(getResources().getColor(R.color.colorDark));
            Typeface typeface = ResourcesCompat.getFont(getApplicationContext(),R.font.aclonica_regular);
            radioBtn.setTypeface(typeface);
            layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,RadioGroup.LayoutParams.MATCH_PARENT);

            radioGroup.addView(radioBtn,layoutParams);

        }
        if(sharedPrefs.getAutoReply(this) == null){
            setRadioBtnChecked(autoReplyList.get(0));
            sharedPrefs.setAutoReply(autoReplyList.get(0),this);
        }
        else{
            setRadioBtnChecked(sharedPrefs.getAutoReply(this));

        }
        layout.addView(radioGroup);
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case(R.id.add_reply):
                if(!isEdit){
                    showAddReplyWindow(view);
                }
                else{
                    rmv_reply.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete));
                    add_reply.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    msg_rmv_reply.setText("");
                    isEdit = false;
                }
                break;

            case(R.id.rmv_reply):
                if(!isEdit){
                    rmv_reply.setImageDrawable(getResources().getDrawable(R.drawable.ic_done));
                    add_reply.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear));
                    msg_rmv_reply.setText("Choose reply and press v\n in order to delete it");
                    isEdit = true;
                }
                else{
                    int radioBtnId = radioGroup.getCheckedRadioButtonId();
                    if(removeRadioBtn(radioBtnId)){
                        showRadioGroup();
                    }
                    else{
                        Toast.makeText(this, "You cannot delete default replies", Toast.LENGTH_SHORT).show();
                    }
                    rmv_reply.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete));
                    add_reply.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    msg_rmv_reply.setText("");
                    isEdit = false;
                }
                break;

            case(R.id.ok_btn_autoReply):
                int radioBtnId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioBtn = findViewById(radioBtnId);
                sharedPrefs.setAutoReply(radioBtn.getText().toString(),getApplication());
                onBackPressed();
                break;

        }

    }

    private void setRadioBtnChecked(String reply){
        RadioButton radioBtn;
        for(int i=0; i<radioGroup.getChildCount(); i++){
            View view = radioGroup.getChildAt(i);
            if (view instanceof RadioButton) {
                radioBtn = (RadioButton) view;
                if(radioBtn.getText().equals(reply)){
                    radioBtn.setChecked(true);
                    break;
                }
            }

        }

    }

    private boolean removeRadioBtn(int radioBtnId){
        RadioButton raddioBtn = findViewById(radioBtnId);
        for(int i=0; i<autoReplyList.size(); i++){
            if(autoReplyList.get(i).equals(raddioBtn.getText())){
                if(i>=0 && i<=3){
                    return false;
                }
                else{
                    autoReplyList.remove(i);
                    sharedPrefs.setAutoReplyList(autoReplyList,this);
                    return true;
                }

            }
        }
        return false; //will never get here
    }

    private void showAddReplyWindow(View view){
        add_reply_dialog.setContentView(R.layout.activity_add_reply);
        final TextView enter_reply = (TextView) add_reply_dialog.findViewById(R.id.enter_reply_txt);
        Button ok_btn = (Button) add_reply_dialog.findViewById(R.id.ok_btn_add_reply);
        TextView exit = (TextView) add_reply_dialog.findViewById(R.id.exit_add_reply_txt);

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(enter_reply.getText().length() == 0){
                    Toast.makeText(AutomaticReplyActivity.this, "You haven't entered a reply", Toast.LENGTH_SHORT).show();
                }
                else{
                    autoReplyList.add(enter_reply.getText().toString());
                    sharedPrefs.setAutoReplyList(autoReplyList,getApplication());
                    showRadioGroup();
                    add_reply_dialog.dismiss();
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_reply_dialog.dismiss();
            }
        });

        add_reply_dialog.show();
    }
}
