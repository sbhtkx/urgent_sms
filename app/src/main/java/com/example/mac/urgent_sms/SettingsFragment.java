package com.example.mac.urgent_sms;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Mac on 16/09/2018.
 */

public class SettingsFragment extends PreferenceFragment{

    private static final int CONTACTS_PERMISSION_CODE = 123;
    private static final int SEND_SMS_PERMISSION_CODE = 456;
    public static CheckBoxPreference enable_contacts;
    private MySharedPreferences sharedPrefs = MySharedPreferences.getInstance();

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        final CheckBoxPreference enable_words = (CheckBoxPreference) findPreference("prefs_enable_words");
        enable_contacts = (CheckBoxPreference) findPreference("prefs_enable_contacts");
        Preference urg_contacts_pref = (Preference) findPreference("prefs_urgent_contacts");
        Preference urg_words_pref = (Preference) findPreference("prefs_urgent_words");
        CheckBoxPreference enable_automatic_reply = (CheckBoxPreference) findPreference("prefs_enable_auto_reply");
        Preference automatic_reply_pref = (Preference) findPreference("prefs_auto_reply");
        RingtonePreference notification_sound = (RingtonePreference) findPreference("prefs_notification_sound");


        enable_contacts.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                    enable_contacts.setChecked(false);
                    requestContactsPermission();
                }

                return true;
            }
        });

        enable_automatic_reply.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                    if(sharedPrefs.getAutoReply(getActivity()) == null){
                        sharedPrefs.setAutoReply(getResources().getString(R.string.automatic_reply_1),getActivity());

                    }

                }

                return true;
            }
        });

        enable_words.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                return true;
            }
        });
        urg_contacts_pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent("com.example.mac.urgent_sms.UrgentContactsActivity");
                startActivity(intent);
                return true;
            }
        });
        urg_words_pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent("com.example.mac.urgent_sms.UrgentWordsActivity");
                startActivity(intent);
                return true;
            }
        });


        automatic_reply_pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent("com.example.mac.urgent_sms.AutomaticReplyActivity");
                startActivity(intent);
                return true;
            }
        });

    }

    private void requestContactsPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)){
            new AlertDialog.Builder(getActivity())
                    .setTitle("Permission needed").setMessage("This permission is needed in order to add contacts")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_CONTACTS},CONTACTS_PERMISSION_CODE);
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
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_CONTACTS},CONTACTS_PERMISSION_CODE);
        }

    }


}
