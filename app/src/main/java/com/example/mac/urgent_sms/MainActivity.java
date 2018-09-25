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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private TextView welcome;
    private ToggleButton enable_switch;
    private Button logout_btn;
    private Button settings;
    private MySharedPreferences sharedPrefs = MySharedPreferences.getInstance();
    private DrawerLayout drawer;
    private static final int DO_NOT_DISTURB_CODE = 456;
    private static boolean has_do_not_disturb_perm = true;
    private static int PERMISSION_ALL = 123;
    private String[] PERMISSIONS = {
            //Manifest.permission.VIBRATE,
            //Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.READ_SMS
    };
    private MsgClassifier msgClassifier;


    NotificationCompat.Builder notification;  // daniel
    private static final int uniqueID = 452345245;  // the system needs it to manage notifications
    int formerMode = 0;  // the ringer mode to back to on completion of ringtone, need to be a field because i don't know other way to send it to onCompletion()

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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
        Picasso.with(this).load(user.getPhotoUrl()).resize(150, 150).into(photo);

        //set default settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        notification = new NotificationCompat.Builder(this, "default"); // not sure about channel_id...
        notification.setAutoCancel(true);

        welcome = (TextView) findViewById(R.id.welcome_txt);
        enable_switch = (ToggleButton) findViewById(R.id.switch_enable_app);
        welcome.setText("Hello                                                                                " + user.getDisplayName() + ",");
        logout_btn = (Button) findViewById(R.id.logout_btn);
        settings = (Button) findViewById(R.id.settings_btn);
        logout_btn.setOnClickListener(this);
        welcome.setOnClickListener(this);
        settings.setOnClickListener(this);

        DataManager dm = DataManager.getInstance(getApplication());
        msgClassifier = MsgClassifier.getInstance(WordsManager.getInstance(dm), dm, getApplication());
        requestDoNotDisturbPermission();


        //set main switch
        enable_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                AudioManager audioManager =
                        (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
                if (isChecked) {
                    enable_switch.setChecked(false);
                    if (has_do_not_disturb_perm) {
                        if (!hasPermissions(getApplication(), PERMISSIONS)) { //checkSelfPermission
                            requestAllPermission();
                        }
                        else { //permission granted
                            enable_switch.setChecked(true);
                            formerMode = audioManager.getRingerMode();
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            SmsReceiver.bindListener(new SmsListener() {
                                @Override
                                public void messageReceived(String messageText, String sender) {
                                    Log.d("msgc1","start");
                                    if (msgClassifier.isUrgent(messageText, null, null)) {
                                        sendNotification(messageText);
                                    } else {
                                        SendSMS sendSMS = new SendSMS(sender, messageText);
                                        sendSMS.sendMsg();
                                    }

                                }
                            });
                        }
                    }
                    else {
                        requestDoNotDisturbPermission();
                    }
                }
                else {
                    sharedPrefs.setSwitchState(false, getApplication());
                    audioManager.setRingerMode(formerMode);
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
            //super.onBackPressed();
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
                Intent share_intent = new Intent(Intent.ACTION_SEND);
                share_intent.setType("text/plain");
                String subject = "Download Urgent SMS";
                String body = "www.google.com";
                share_intent.putExtra(Intent.EXTRA_SUBJECT,subject);
                share_intent.putExtra(Intent.EXTRA_TEXT,body);
                startActivity(Intent.createChooser(share_intent, "Share using"));

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

    private void requestDoNotDisturbPermission() {
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivityForResult(intent,DO_NOT_DISTURB_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DO_NOT_DISTURB_CODE ) {
            NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()){
                Toast.makeText(this, "Do not disturb Permission denied", Toast.LENGTH_SHORT).show();
                onBackPressed();
                has_do_not_disturb_perm = false;
            }
            else{
                onBackPressed();
                has_do_not_disturb_perm = true;
                Toast.makeText(this, "Do not disturb Permission given", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private void requestAllPermission(){
        if( (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS))  ){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed").setMessage("This permission is needed in order to use this app")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS,PERMISSION_ALL);

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
            ActivityCompat.requestPermissions(this,PERMISSIONS,PERMISSION_ALL);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_ALL){
            if( (grantResults.length > 0) && (grantResults[0]==PackageManager.PERMISSION_GRANTED) ){

                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                enable_switch.setChecked(true);
                sharedPrefs.setSwitchState(true, getApplication());


            }
            else{
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
                enable_switch.setChecked(false);
                sharedPrefs.setSwitchState(false,getApplication());


            }
        }
    }




    private void sendNotification(String msg) {



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
//        MediaPlayer mp = MediaPlayer.create(this, R.raw.short_sms);
        String yourFilePath = "content://media/internal/audio/media/32";
//        Uri uri =
        File yourFile = new File( yourFilePath );
        Uri uri = Uri.fromFile(yourFile);
        MediaPlayer mp = MediaPlayer.create(getApplication(), uri);

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
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
            }

        });
    }

}
