package com.example.mac.urgent_sms;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private int year, month, day, hour, minute;
    private int day_of_monthFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    private TextView display;
    private MySharedPreferences sharedPrefs = MySharedPreferences.getInstance();
    private ArrayList<Date> dates;
    ListView listView_alarms;
    private CustomAdapter customAdapter = new CustomAdapter();
    private static boolean isEdit = false;
    private static int itemToEdit;
    private Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        listView_alarms = (ListView) findViewById(R.id.listView_alarms);
        final ImageView add_alarm = (ImageView) findViewById(R.id.add_alarm_btn);

        dates = sharedPrefs.getTimerList(this);

        rmvPastAlarms();

        listView_alarms.setAdapter(customAdapter);

        registerForContextMenu(listView_alarms);

        add_alarm.setOnClickListener(this);

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listView_alarms) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for(int i=0; i<menuItems.length; i++){
                menu.add(Menu.NONE,i,i,menuItems[i]);
            }

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        switch(menuItemIndex){
            case(0):
                isEdit = true;
                itemToEdit = info.position;
                year = dates.get(info.position).getYear();
                month = dates.get(info.position).getMonth();
                day = dates.get(info.position).getDayOfMonth();
                DatePickerDialog datePickerDialog = new DatePickerDialog(CalendarActivity.this,CalendarActivity.this,year,month,day);
                datePickerDialog.show();
                break;

            case(1):
                dates.remove(info.position);
                sharedPrefs.setTimerList(dates,getApplication());
                listView_alarms.setAdapter(customAdapter);
                cancelAlarm(info.position);
                break;
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case(R.id.add_alarm_btn):
                isEdit = false;
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CalendarActivity.this,CalendarActivity.this,year,month,day);
                datePickerDialog.show();
                break;

        }

    }



    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1;
        day_of_monthFinal = i2;


        c = Calendar.getInstance();
        c.set(Calendar.YEAR,yearFinal);
        c.set(Calendar.MONTH,monthFinal);
        c.set(Calendar.DAY_OF_MONTH,day_of_monthFinal);
        if (isSoonerThanToday(c)){
            Toast.makeText(this, "You cannot select date from the past", Toast.LENGTH_SHORT).show();
            DatePickerDialog datePickerDialog = new DatePickerDialog(CalendarActivity.this,CalendarActivity.this,year,month,day);
            datePickerDialog.show();
        }

        else{
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(CalendarActivity.this,CalendarActivity.this,hour,minute, true);
            timePickerDialog.show();
        }


    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;

        c.set(Calendar.HOUR_OF_DAY,hourFinal);
        c.set(Calendar.MINUTE,minuteFinal);
        if(isSoonerThanToday(c)){
            TimePickerDialog timePickerDialog = new TimePickerDialog(CalendarActivity.this,CalendarActivity.this,hour,minute, true);
            timePickerDialog.show();
            Toast.makeText(this, "You cannot select time from the past", Toast.LENGTH_SHORT).show();

        }

        else{
            if(!isEdit){
                Date date = new Date(c);
                startAlarm(date);
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
            sharedPrefs.setTimerList(dates,this);
        }


    }


    private boolean isSoonerThanToday(Calendar c){
        Calendar calendar_now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,day);


        if (calendar_now.getTime().after(c.getTime())) {
            return true;
        }
        else{
            return false;
        }


    }


    private void startAlarm(Date date){

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,date.getId(),intent,0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,date.getCalendar().getTimeInMillis(),pendingIntent);


    }

    private void cancelAlarm(int id){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,id,intent,0);

        alarmManager.cancel(pendingIntent);
    }

    private void rmvPastAlarms(){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.SECOND,0);
        ArrayList<Date> dates_to_remove = new ArrayList<>();
        for(Date date : dates){
            if(date.getCalendar().getTimeInMillis() < today.getTimeInMillis()){
                dates_to_remove.add(date);
            }
        }
        dates.removeAll(dates_to_remove);
        sharedPrefs.setTimerList(dates,this);
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
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

            isOn.setChecked(dates.get(i).getIsOn());

            isOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        hour_txt.setTextColor(getResources().getColor(R.color.blackColor));
                        date_txt.setTextColor(getResources().getColor(R.color.blackColor));
                        Date date = dates.get(i);
                        date.setIsOn(true);
                        dates.set(i,date);
                        sharedPrefs.setTimerList(dates,CalendarActivity.this);
                        startAlarm(date);

                    }
                    else{
                        hour_txt.setTextColor(getResources().getColor(R.color.grayColor));
                        date_txt.setTextColor(getResources().getColor(R.color.grayColor));
                        Date date = dates.get(i);
                        date.setIsOn(false);
                        dates.set(i,date);
                        sharedPrefs.setTimerList(dates,CalendarActivity.this);
                        cancelAlarm(date.getId());

                    }
                }
            });



            return view;
        }
    }
}
