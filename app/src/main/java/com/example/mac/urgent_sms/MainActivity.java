package com.example.mac.urgent_sms;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private TextView welcome;
    private ToggleButton enable_switch;
    private Button logout_btn;
    private Button settings;
    final static int SMS_PERMISSION_CODE = 1;
    private MySharedPreferences sharedPrefs = MySharedPreferences.getInstance();
    private DrawerLayout drawer;






    NotificationCompat.Builder notification;  // daniel
    private static final int uniqueID = 452345245;  // the system needs it to manage notifications
    int formerMode =0;  // the ringer mode to back to on completion of ringtone, need to be a field because i don't know other way to send it to onCompletion()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //set toolbar
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //set navigation bar
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //set navigation header
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.my_name);
        navUsername.setText(user.getDisplayName());
        TextView navEmail = (TextView) headerView.findViewById(R.id.my_email);
        navEmail.setText(user.getEmail());
        ImageView photo = (ImageView) headerView.findViewById(R.id.my_pic);
        Picasso.with(this).load(user.getPhotoUrl()).resize(150,150).into(photo);

        //set default settings
        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);

        notification = new NotificationCompat.Builder(this,"default"); // not sure about channel_id...
        notification.setAutoCancel(true);

        welcome = (TextView) findViewById(R.id.welcome_txt);
        enable_switch = (ToggleButton) findViewById(R.id.switch_enable_app);
        welcome.setText("Hello                                                                                " +user.getDisplayName()+",");
        logout_btn= (Button) findViewById(R.id.logout_btn);
        settings = (Button) findViewById(R.id.settings_btn);
        logout_btn.setOnClickListener(this);
        welcome.setOnClickListener(this);
        settings.setOnClickListener(this);
//daniel
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
// till here
        //set main switch
        enable_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

//            AudioManager audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
            DataManager dm = new DataManager(getApplicationContext());
            WordsManager wm = WordsManager.getInstance(dm);
            MsgClassifier msgClassifier = MsgClassifier.getInstance(wm, dm, getApplication());

            public void onCheckedChanged(CompoundButton button, boolean isChecked){
                if(isChecked){

//                    formerMode = audioManager.getRingerMode();
                    sharedPrefs.setSwitchState(true,getApplication());
//                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                    //checks for permission
                    if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
                        enable_switch.setChecked(false);
                        requestSMSPermission();
                    }
                    else{ //permission granted
                        enable_switch.setChecked(true);
                        sharedPrefs.setSwitchState(true,getApplication());
//                        SmsReceiver.bindListener(new SmsListener() {
//                            @Override
//                            public void messageReceived(String messageText, String sender) {
//
//                                if(msgClassifier.isUrgent(messageText,null,null)){
//                                    sendNotification(messageText);
//                                }
//                                else{
//                                    SendSMS sendSMS = new SendSMS(sender,messageText);
//                                    sendSMS.sendMsg();
//                                }
//                            }
//                        });
                    }

                }
                else{
                    sharedPrefs.setSwitchState(false,getApplication());
//                    audioManager.setRingerMode(formerMode);
                }

            }

        });

    } //end of onCreate

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(sharedPrefs.getSwitchState(this)){
            enable_switch.setChecked(true);
        }
        else{
            enable_switch.setChecked(false);
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case(R.id.nav_settings):
                intent =  new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);

                break;

            case(R.id.nav_share):
                //intent = new Intent(MainActivity.this,
                break;


            case(R.id.nav_about):
                intent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent);
                break;

            case(R.id.nav_contact_us):
                intent = new Intent(MainActivity.this,ContactUsActivity.class);
                startActivity(intent);
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                Intent main_intent= new Intent("com.example.mac.urgent_sms.SignUpActivity");
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    startActivity(main_intent);
                    Toast.makeText(this,"user==null",Toast.LENGTH_LONG).show();

                }

                break;

            case (R.id.welcome_txt):
                Intent myAccount_intent = new Intent("com.example.mac.urgent_sms.MyAccountActivity");
                startActivity(myAccount_intent);
                break;



        }
    }


    private void requestSMSPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_SMS)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed").setMessage("This permission is needed in order to use this app")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.RECEIVE_SMS},SMS_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();

        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECEIVE_SMS},SMS_PERMISSION_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == SMS_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                enable_switch.setChecked(true);
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendNotification(String msg){

              /*  NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }*/



        // Build the notification
        notification.setSmallIcon(R.drawable.alert);//
        notification.setTicker("Urgent message!");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Urgent message!");
        notification.setContentText(msg);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        // Builds notification and issues it
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());

        // play ringtone
        MediaPlayer mp = MediaPlayer.create(this, R.raw.short_sms);
        AudioManager audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                AudioManager audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
                audioManager.setRingerMode(formerMode);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
                }else{
                    //deprecated in API 26
                    v.vibrate(500);
                }
            }

        });
    }

}
