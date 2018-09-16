package com.example.mac.urgent_sms;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

/**
 * Created by Mac on 16/09/2018.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        Preference urg_contacts_pref = (Preference) findPreference("pref_urgent_contacts");
        Preference urg_words_pref = (Preference) findPreference("pref_urgent_words");
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

    }
}
