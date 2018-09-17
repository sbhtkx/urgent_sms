package com.example.mac.urgent_sms;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UrgentWordsActivity2 extends AppCompatActivity implements View.OnClickListener {

    private ImageButton add_word_btn;
    private ListView listView_urg_words;
    private ArrayList<Contact> words = new ArrayList<Contact>();
    private MyDatabase my_database = MyFirebaseDatabase.getInstance();

    private static final int WORDS_REQUEST_CODE = 1;  // ???


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urgent_words);

        listView_urg_words = (ListView) findViewById(R.id.listView_urg_words);
        add_word_btn = (ImageButton) findViewById(R.id.img_add_words);
        final TextView no_urgent_words = (TextView) findViewById(R.id.no_words_txt);
        add_word_btn.setOnClickListener(this);

        my_database.getWordList(new MyCallback<ArrayList<Contact>>() {
            @Override
            public void onSuccess(ArrayList<Contact> data) {
                words = data;

                if (words.size() == 0) {
                    no_urgent_words.setText("No urgent words");
                } else {
                    no_urgent_words.setText("");
                }
                CustomAdapter customAdapter = new CustomAdapter();
                listView_urg_words.setAdapter(customAdapter);
            }
        });
    }
    @Override
    public void onClick(View view) {

    }

    class CustomAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return words.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.one_word_from_list,null);
            final TextView contact_name = (TextView) view.findViewById(R.id.contact_name);
            TextView contact_phone = (TextView) view.findViewById(R.id.contact_phone);
            ImageButton rmv_contact = (ImageButton) view.findViewById(R.id.rmv_contact_btn);

            contact_name.setText(contacts.get(i).getName());
            contact_phone.setText(contacts.get(i).getPhoneNumber());

            rmv_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contacts.remove(i);
                    my_database.setContactList(contacts);
                }
            });

            return view;
        }

    }
}
