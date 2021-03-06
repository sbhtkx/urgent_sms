package com.example.mac.urgent_sms;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
import android.telephony.SmsManager;
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

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener, SmsListener {

    private TextView welcome;
    public static ToggleButton enable_switch;
    private Button logout_btn;
    private Button settings;
    private MySharedPreferences sharedPrefs = MySharedPreferences.getInstance();
    private DrawerLayout drawer;
    private static final int DO_NOT_DISTURB_CODE = 456;
    private static int READ_SMS_PERMISSION_CODE = 123;
    private MsgClassifier msgClassifier;
    NotificationCompat.Builder notificationBuilder;  // daniel
    private static final int uniqueID = 452345245;  // the system needs it to manage notifications
    int formerMode;  // the ringer mode to back to on completion of ringtone, need to be a field because i don't know other way to send it to onCompletion()
    private Uri uriFormerRingtone;

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

        notificationBuilder = new NotificationCompat.Builder(this, "default"); // not sure about channel_id...
        notificationBuilder.setAutoCancel(true);

        welcome = (TextView) findViewById(R.id.welcome_txt);
        enable_switch = (ToggleButton) findViewById(R.id.switch_enable_app);
        welcome.setText("Hello                                                                                " + user.getDisplayName() + ",");
        logout_btn = (Button) findViewById(R.id.logout_btn);
        settings = (Button) findViewById(R.id.settings_btn);
        logout_btn.setOnClickListener(this);
        settings.setOnClickListener(this);

        DataManager dm = DataManager.getInstance(getApplication());
        msgClassifier = MsgClassifier.getInstance(WordsManager.getInstance(dm), dm, getApplication());

        // Build the notification for received urgent sms
        notificationBuilder = new NotificationCompat.Builder(this, "default"); // not sure about channel_id...
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.drawable.alert);//
        notificationBuilder.setTicker("Urgent message!");
        notificationBuilder.setWhen(System.currentTimeMillis());
        notificationBuilder.setContentTitle("Urgent message!");
        notificationBuilder.setSound(Uri.parse("android.resource://" + getPackageName() + "/raw/five_seconds_of_silence.mp3"));
//        notificationBuilder.setSound(Uri.parse(sharedPrefs.getRingtoneLocation(MainActivity.this)));

        // build what will happen when user press on the notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        SmsReceiver.bindListener(this);
        requestDoNotDisturbPermission();
        requestSettingPermission();
        final SmsReceiver smsReceiver = new SmsReceiver();


        //set main switch
        enable_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked) {
                    sharedPrefs.setSwitchState(true, getApplication());
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || notificationManager.isNotificationPolicyAccessGranted()) {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                            requestSMSPermission();
                        } else { //permission granted

                            //register receiver
                            IntentFilter filter = new IntentFilter();
                            filter.addAction("android.provider.Telephony.SMS_RECEIVED");
                            registerReceiver(smsReceiver, filter);

                            Toast.makeText(MainActivity.this, "else", Toast.LENGTH_SHORT).show();
                            changeToSilentAndChangeRingtone();
                        }
                    } else { //do not have permission
                        requestDoNotDisturbPermission();
                        enable_switch.setChecked(false);
                        sharedPrefs.setSwitchState(false, getApplication());
                    }
                } else { //!isChecked
                    unregisterReceiver(smsReceiver);
                    sharedPrefs.setSwitchState(false, getApplication());
                    changeBackRingerModeAndRingtone();
                }

            }

        });

    } //end of onCreate


    @Override
    public void messageReceived(String messageText, String sender) {
        ArrayList<Contact> contacts = null;
        ArrayList<Word> words = null;
        if (sharedPrefs.getContactsState(this)) {
            contacts = sharedPrefs.getContactList(this);
        }
        if (sharedPrefs.getWordsState(this)) {
            words = sharedPrefs.getUrgentWordsList(this);
        }
        if (msgClassifier.isUrgent(messageText, contacts, words)) {
            sendNotification(messageText);
        } else { //msg is not urgent
            sendMsg(sender);
        }

    }

    private void sendMsg(String sender) {
        if (sharedPrefs.getAutoReplyState(this)) {
            Calendar prior_msg_time = sharedPrefs.getPriorMsgTime(this);
            sharedPrefs.setPriorMsgTime(Calendar.getInstance(), this);
            Calendar msg_now = Calendar.getInstance();
            if (prior_msg_time != null) {
                long dif_sec = (msg_now.getTimeInMillis() - prior_msg_time.getTimeInMillis()) / 1000;
                if (dif_sec >= 10) {
                    Toast.makeText(getBaseContext(), "sending the message", Toast.LENGTH_SHORT).show();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(sender, null, sharedPrefs.getAutoReply(this), null, null);
                }
            } else {
                Toast.makeText(getBaseContext(), "sending the message", Toast.LENGTH_SHORT).show();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(sender, null, sharedPrefs.getAutoReply(this), null, null);
            }


        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if ((sharedPrefs.getSwitchState(this)) || (sharedPrefs.getHasTimerEnableApp(this))) {
            enable_switch.setChecked(true);
            if (sharedPrefs.getHasTimerEnableApp(this)) {
                moveTaskToBack(true);
                sharedPrefs.setHasTimerEnableApp(false, this);

            }
        } else {
            enable_switch.setChecked(false);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case (R.id.nav_settings):
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);

                break;

            case (R.id.nav_share):
                Intent share_intent = new Intent(Intent.ACTION_SEND);
                share_intent.setType("text/plain");
                String subject = "Download Urgent SMS";
                String body = "www.google.com";
                share_intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                share_intent.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(share_intent, "Share using"));

                break;


            case (R.id.nav_about):
                intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;

            case (R.id.nav_contact_us):
                intent = new Intent(MainActivity.this, ContactUsActivity.class);
                startActivity(intent);
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.logout_btn):
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //user is now signed out
                        finish();
                    }
                });
                Toast.makeText(this, "signed-out", Toast.LENGTH_LONG).show();
                Intent main_intent = new Intent("com.example.mac.urgent_sms.SignUpActivity");
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    startActivity(main_intent);
                    Toast.makeText(this, "user==null", Toast.LENGTH_LONG).show();

                }

                break;


        }
    }

    private void requestDoNotDisturbPermission() {
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivityForResult(intent, DO_NOT_DISTURB_CODE);
            onBackPressed();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DO_NOT_DISTURB_CODE) {
            NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {
                Toast.makeText(this, "Do not disturb Permission denied", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Do not disturb Permission GRANTED", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void requestSMSPermission() {
        enable_switch.setChecked(false);
        sharedPrefs.setSwitchState(false, this);
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed").setMessage("This permission is needed in order to send automatic reply")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, READ_SMS_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, READ_SMS_PERMISSION_CODE);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enable_switch.setChecked(true);
                sharedPrefs.setSwitchState(true, this);
                Toast.makeText(this, "SMS Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS Permission DENIED", Toast.LENGTH_SHORT).show();

            }
        }

    }


    private void sendNotification(String msg) {
        notificationBuilder.setContentText(msg);

        // Builds notification and issues it
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notificationBuilder.build());

        // play ringtone


        if (sharedPrefs.getRingtoneState(this)) {
            Log.d("send1", "ok");
            String pathNewRingtone = sharedPrefs.getRingtoneLocation(this);
            Uri uriNewRingtone = Uri.parse(pathNewRingtone);
            MediaPlayer mp = MediaPlayer.create(getApplication(), R.raw.five_seconds_of_silence);
            final AudioManager audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
            final NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || notificationManager.isNotificationPolicyAccessGranted()) {
                Log.d("send1", "BLA");
                audioManager.setRingerMode(2);
            }
            Log.d("send1", Integer.toString(audioManager.getRingerMode()));
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(getApplicationContext())) {
                Log.d("rinn2", ""+RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this,RingtoneManager.TYPE_NOTIFICATION));
                RingtoneManager.setActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_NOTIFICATION, uriNewRingtone);
            }
            // Vibrate for 500 milliseconds
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            else {
                //deprecated in API 26
                v.vibrate(500);
            }
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || notificationManager.isNotificationPolicyAccessGranted()) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    }
                }
            });
        }
    }


    private void changeToSilentAndChangeRingtone() {
        AudioManager audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
        // change ringer mode
        formerMode = audioManager.getRingerMode();
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        // change ringtone
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(MainActivity.this)) {
            uriFormerRingtone = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);
            String pathNewRingtone = sharedPrefs.getRingtoneLocation(MainActivity.this);
            Uri uriNewRingtone = Uri.parse(pathNewRingtone);
//            RingtoneManager.setActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_NOTIFICATION, uriNewRingtone);
            Toast.makeText(this, "" + RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_NOTIFICATION), Toast.LENGTH_SHORT).show();
        }
    }

    private void changeBackRingerModeAndRingtone() {
        AudioManager audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
        // change back the ringer mode
        audioManager.setRingerMode(formerMode);
        // change back the ringtone
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(MainActivity.this))) {
            Log.d("rinn1", ""+RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this,RingtoneManager.TYPE_NOTIFICATION));
            RingtoneManager.setActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_NOTIFICATION, uriFormerRingtone);
        }
    }

    private void requestSettingPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 200);

            }
        }
    }


}
