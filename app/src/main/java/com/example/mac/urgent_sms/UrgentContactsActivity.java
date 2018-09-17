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

public class UrgentContactsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton add_contact_btn;
    private  ListView listView_urg_contacts;
    private ArrayList<Contact> contacts = new ArrayList<Contact>();
    private MySharedPreferences sharedPrefs = MySharedPreferences.getInstance();
    private CustomAdapter customAdapter = new CustomAdapter();

    private static final int CONTANTS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urgent_contacts);

        listView_urg_contacts = (ListView) findViewById(R.id.listView_urg_contacts);
        add_contact_btn = (ImageButton) findViewById(R.id.img_add_contacts);
        final TextView no_urgent_contacts = (TextView) findViewById(R.id.no_contacts_txt);
        add_contact_btn.setOnClickListener(this);


        contacts = sharedPrefs.getContactList(this);
        listView_urg_contacts.setAdapter(customAdapter);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CONTANTS_REQUEST_CODE && resultCode == RESULT_OK){
            onContactPicked(data);
        }
    }

    private void onContactPicked(Intent data) {


        Cursor cursor = null;
        try {
            String contact_phoneNo =null;
            String contact_name;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();

            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);

            if(cursor.moveToFirst() ){
                int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                contact_name = cursor.getString(nameIndex);

                int columnIndex_ID = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                String contactID = cursor.getString(columnIndex_ID);

                Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (hasPhone > 0) {
                    Cursor cursorNum = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactID,
                            null,
                            null);
                    int type = cursorNum.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    //Toast.makeText(this, ""+type, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(this, ""+ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, Toast.LENGTH_SHORT).show();
                    if(cursorNum.moveToNext()){

                        int columnIndex_number = cursorNum.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        contact_phoneNo = cursorNum.getString(columnIndex_number);

                    }

                    else{
                        Toast.makeText(this, "No mobile phone", Toast.LENGTH_SHORT).show();
                    }

                }
                Contact contact = new Contact(contact_name,contact_phoneNo);
                if(isContactAlreadyExist(contact_phoneNo)){
                    Toast.makeText(this, contact.getName()+" is already in your urgent list", Toast.LENGTH_SHORT).show();
                }
                else{
                    contacts.add(contact);
                    sharedPrefs.setContactList(contacts,this);
                    listView_urg_contacts.setAdapter(customAdapter);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.img_add_contacts){
            Intent contact_picker = new Intent(Intent.ACTION_PICK);
            contact_picker.setType(ContactsContract.Contacts.CONTENT_TYPE);
            startActivityForResult(contact_picker, CONTANTS_REQUEST_CODE);
        }
    }

    class CustomAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return contacts.size();
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
            view = getLayoutInflater().inflate(R.layout.one_contact_from_list,null);
            final TextView contact_name = (TextView) view.findViewById(R.id.contact_name);
            TextView contact_phone = (TextView) view.findViewById(R.id.contact_phone);
            ImageButton rmv_contact = (ImageButton) view.findViewById(R.id.rmv_contact_btn);

            contact_name.setText(contacts.get(i).getName());
            contact_phone.setText(contacts.get(i).getPhoneNumber());

            rmv_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contacts.remove(i);
                    sharedPrefs.setContactList(contacts,getApplication());
                    listView_urg_contacts.setAdapter(customAdapter);

                }
            });

            return view;
        }

    }


    private boolean isContactAlreadyExist(String phoneNum){
        for(Contact contact : contacts){
            if(contact.getPhoneNumber().equals(phoneNum)){
                return true;
            }
        }
        return false;
    }
}
