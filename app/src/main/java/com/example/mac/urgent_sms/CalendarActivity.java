package com.example.mac.urgent_sms;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private int day_of_monthFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    private TextView display;
    private MySharedPreferences sharedPrefs = MySharedPreferences.getInstance();
    private ArrayList<Date> dates;
    ListView listView_alarms;
    private CustomAdapter customAdapter = new CustomAdapter();
    private static boolean isEdit = false;
    private static int itemToEdit;
    private static boolean isDeleteMode = false;
    private static int itemToDelete;
    private ImageView delete_btn;
    private ImageView cancel_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        delete_btn = (ImageView) findViewById(R.id.delete_btn);
        cancel_btn = (ImageView) findViewById(R.id.cancel_btn);
        delete_btn.setVisibility(View.INVISIBLE);
        cancel_btn.setVisibility(View.INVISIBLE);

        listView_alarms = (ListView) findViewById(R.id.listView_alarms);
        final ImageView add_alarm = (ImageView) findViewById(R.id.add_alarm_btn);

        dates = sharedPrefs.getDateList(this);

        listView_alarms.setAdapter(customAdapter);


        listView_alarms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                isEdit = true;
                itemToEdit = i;
                int year = dates.get(i).getYear();
                int month = dates.get(i).getMonth();
                int day = dates.get(i).getDayOfMonth();
                DatePickerDialog datePickerDialog = new DatePickerDialog(CalendarActivity.this,CalendarActivity.this,year,month,day);
                datePickerDialog.show();

            }
        });

        listView_alarms.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                delete_btn.setVisibility(View.VISIBLE);
                cancel_btn.setVisibility(View.VISIBLE);
                isDeleteMode = true;
                itemToDelete = i;
                return true;
            }
        });

        cancel_btn.setOnClickListener(this);
        add_alarm.setOnClickListener(this);
        delete_btn.setOnClickListener(this);


    }



    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1;
        day_of_monthFinal = i2;

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(CalendarActivity.this,CalendarActivity.this,hour,minute, true);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;

        if(!isEdit){
            Date date = new Date(yearFinal, monthFinal,day_of_monthFinal, hourFinal, minuteFinal);
            dates.add(date);
        }
        else{
            dates.get(itemToEdit).setYear(yearFinal);
            dates.get(itemToEdit).setMonth(monthFinal);
            dates.get(itemToEdit).setDay(day_of_monthFinal);
            dates.get(itemToEdit).setHour(hourFinal);
            dates.get(itemToEdit).setMinute(minuteFinal);
            isEdit = false;
        }
        listView_alarms.setAdapter(customAdapter);
        sharedPrefs.setDateList(dates,this);


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case(R.id.cancel_btn):
                delete_btn.setVisibility(View.INVISIBLE);
                cancel_btn.setVisibility(View.INVISIBLE);
                break;

            case(R.id.add_alarm_btn):
                isEdit = false;
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CalendarActivity.this,CalendarActivity.this,year,month,day);
                datePickerDialog.show();
                break;

            case(R.id.delete_btn):
                delete_btn.setVisibility(View.INVISIBLE);
                cancel_btn.setVisibility(View.INVISIBLE);
                dates.remove(itemToDelete);
                sharedPrefs.setDateList(dates,getApplication());
                listView_alarms.setAdapter(customAdapter);
                break;
        }



    }

    class CustomAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return dates.size();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.one_alarm_from_list,null);
            final TextView hour_txt = (TextView) view.findViewById(R.id.time_txt);
            final TextView date_txt = (TextView) view.findViewById(R.id.date_txt);
            Switch isOn = (Switch) view.findViewById(R.id.switch_isOn);

            String minute_format = dates.get(i).getMinute()+"";
            if(minute_format.length() == 1){
                minute_format = "0"+minute_format;
            }

            String hour_format = dates.get(i).getHour()+"";
            if(hour_format.length() == 1){
                hour_format = "0"+hour_format;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(dates.get(i).getYear(),dates.get(i).getMonth(),dates.get(i).getDayOfMonth());
            String date_format = DateFormat.getDateInstance().format(calendar.getTime());
            hour_txt.setText(hour_format+":"+minute_format);
            date_txt.setText(date_format);

            isOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        hour_txt.setTextColor(getResources().getColor(R.color.blackColor));
                        date_txt.setTextColor(getResources().getColor(R.color.blackColor));
                    }
                    else{
                        hour_txt.setTextColor(getResources().getColor(R.color.grayColor));
                        date_txt.setTextColor(getResources().getColor(R.color.grayColor));
                    }
                }
            });



            return view;
        }
    }
}
