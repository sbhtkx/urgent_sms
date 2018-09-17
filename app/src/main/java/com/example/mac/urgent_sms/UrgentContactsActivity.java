package com.example.mac.urgent_sms;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UrgentContactsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton add_contact_btn;
    private  ListView listView_urg_contacts;
    private ArrayList<Contact> contacts = new ArrayList<Contact>();
    private MySharedPreferences sharedPrefs = MySharedPreferences.getInstance();
    private CustomAdapter customAdapter = new CustomAdapter();
    private Dialog dialog_urgency_level;

    private static final int CONTANTS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urgent_contacts);

        listView_urg_contacts = (ListView) findViewById(R.id.listView_urg_contacts);
        add_contact_btn = (ImageButton) findViewById(R.id.img_add_contacts);
        final TextView no_urgent_contacts = (TextView) findViewById(R.id.no_contacts_txt);
        dialog_urgency_level = new Dialog(this);
        add_contact_btn.setOnClickListener(this);


        contacts = sharedPrefs.getContactList(this);
        listView_urg_contacts.setAdapter(customAdapter);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CONTANTS_REQUEST_CODE && resultCode == RESULT_OK){
            dialog_urgency_level.setContentView(R.layout.activity_add_contact);
            onContactPicked(data);
            UrgencyLevelDialog();

        }
    }

    private void UrgencyLevelDialog(){
        final RadioGroup radioGroup = (RadioGroup) dialog_urgency_level.findViewById(R.id.rBtn_contact_urgency_level);
        TextView exit = (TextView) dialog_urgency_level.findViewById(R.id.exit_contact_txt);
        Button ok_btn = (Button) dialog_urgency_level.findViewById(R.id.ok_contact_urgency_level_btn);

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioBtnId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton =  dialog_urgency_level.findViewById(radioBtnId);

                String urgency_level = radioButton.getText().toString();
                switch (urgency_level){
                    case("urgent"):
                        contacts.get(contacts.size()-1).setUrgencyLevel(1);
                        break;
                    case("very urgent"):
                        contacts.get(contacts.size()-1).setUrgencyLevel(2);
                        break;
                    case("extremely urgent"):
                        contacts.get(contacts.size()-1).setUrgencyLevel(3);
                        break;
                }
                sharedPrefs.setContactList(contacts,getApplication());
                listView_urg_contacts.setAdapter(customAdapter);
                dialog_urgency_level.dismiss();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_urgency_level.dismiss();
            }
        });

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
                    dialog_urgency_level.show();
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
            TextView urgency_level = (TextView) view.findViewById(R.id.urgency_level_contact_txt);
            ImageButton rmv_contact = (ImageButton) view.findViewById(R.id.rmv_contact_btn);

            contact_name.setText(contacts.get(i).getName());
            contact_phone.setText(contacts.get(i).getPhoneNumber());
            switch(contacts.get(i).getUrgencyLevel()){
                case(1):
                    urgency_level.setText("urgent");
                    urgency_level.setTextColor(getResources().getColor(R.color.yellowColor));
                    break;

                case(2):
                    urgency_level.setText("very urgent");
                    urgency_level.setTextColor(getResources().getColor(R.color.orangeColor));
                    break;

                case(3):
                    urgency_level.setText("extremely urgent");
                    urgency_level.setTextColor(getResources().getColor(R.color.redColor));
                    break;
            }

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
