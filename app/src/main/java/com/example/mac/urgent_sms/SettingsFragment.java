package com.example.mac.urgent_sms;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Mac on 16/09/2018.
 */

public class SettingsFragment extends PreferenceFragment {

    private static final int CONTACTS_PERMISSION_CODE = 123;
    private MyDatabase my_database = MyFirebaseDatabase.getInstance();

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        Preference urg_contacts_pref = (Preference) findPreference("pref_urgent_contacts");
        Preference urg_words_pref = (Preference) findPreference("pref_urgent_words");
        urg_contacts_pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                    requestContactsPermission();
                }
                else{
                    Intent intent = new Intent("com.example.mac.urgent_sms.UrgentContactsActivity");
                    startActivity(intent);
                }


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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CONTACTS_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getActivity(), "Permission GRANTED", Toast.LENGTH_SHORT).show();
                my_database.setContactList(new ArrayList<Contact>());
                Intent intent = new Intent("com.example.mac.urgent_sms.UrgentContactsActivity");
                startActivity(intent);
            }
            else{
                Toast.makeText(getActivity(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
