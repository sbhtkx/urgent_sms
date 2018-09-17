package com.example.mac.urgent_sms;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UrgentWordsActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView listView_urg_words;
    private MySharedPreferences sharedPrefs = MySharedPreferences.getInstance();
    private ArrayList<Word> urgentWords_list;
    private CustomAdapter customAdapter = new CustomAdapter();
    private Dialog dialog_add_word;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urgent_words);

        ImageButton add_word_btn = (ImageButton) findViewById(R.id.add_word_btn);
        listView_urg_words = (ListView) findViewById(R.id.listView_urg_words);

        urgentWords_list = sharedPrefs.getUrgentWordsList(this);
        listView_urg_words.setAdapter(customAdapter);

        dialog_add_word = new Dialog(this);

        add_word_btn.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.add_word_btn){
            showAddWordWindow(view);

        }
    }

    private void showAddWordWindow(View view){
        dialog_add_word.setContentView(R.layout.activity_add_word);
        final RadioGroup radioGroup = (RadioGroup) dialog_add_word.findViewById(R.id.rBtn_urgency_level);
        Button ok_btn = (Button) dialog_add_word.findViewById(R.id.ok_urgency_level_btn);
        TextView exit = (TextView) dialog_add_word.findViewById(R.id.exit_txt);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) dialog_add_word.findViewById(radioId);
                int urgency_level = 1;
                switch (radioButton.getText().toString()) {
                    case ("urgent"):
                        urgency_level = 1;
                        break;

                    case ("very urgent"):
                        urgency_level = 2;
                        break;

                    case ("extremely urgent"):
                        urgency_level = 3;
                        break;
                }
                EditText enter_word = (EditText) dialog_add_word.findViewById(R.id.enter_word_txt);
                String new_word = enter_word.getText().toString();
                if(new_word.length()==0){
                    Toast.makeText(UrgentWordsActivity.this, "You haven't entered a word", Toast.LENGTH_SHORT).show();
                }
                else if(!isWordAlreadyExist(new_word)){
                    //save the new word
                    Word word = new Word(new_word,urgency_level);
                    urgentWords_list.add(word);
                    sharedPrefs.setUrgentWordsList(urgentWords_list,getApplication());
                    listView_urg_words.setAdapter(customAdapter);
                    dialog_add_word.dismiss();
                }
                else{
                    Toast.makeText(UrgentWordsActivity.this, new_word+" already exist in your urgent list", Toast.LENGTH_SHORT).show();
                }


            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_add_word.dismiss();
            }
        });


        dialog_add_word.show();

        }



    class CustomAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return urgentWords_list.size();
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
            TextView word_txt = (TextView) view.findViewById(R.id.word_txt);
            TextView urgency_level = (TextView) view.findViewById(R.id.urgency_level_txt);
            ImageButton rmv_word_btn = (ImageButton) view.findViewById(R.id.rmv_word_btn);

            word_txt.setText(urgentWords_list.get(i).getWord());
            switch(urgentWords_list.get(i).getUrgencyLevel()){
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
            rmv_word_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    urgentWords_list.remove(i);
                    sharedPrefs.setUrgentWordsList(urgentWords_list,getApplication());
                    listView_urg_words.setAdapter(customAdapter);
                }
            });


            return view;
        }
    }

    private boolean isWordAlreadyExist(String word){
        boolean ans = false;
        for (Word _word : urgentWords_list){
            if(_word.getWord().equals(word)){
                ans = true;
            }
        }
        return ans;
    }
}
