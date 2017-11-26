package com.appdevelapp.samplealarmapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String CLASS_TAG = MainActivity.class.getSimpleName();

    public static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 123;
    Button mShowDateTimeDialogButton;
    Button mAddReminder;

    Calendar timeSelected = null;

    Boolean isEvent = true;

    int year;
    int month;
    int day;
    int hour;
    int minute;

    final static int req1=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShowDateTimeDialogButton = findViewById(R.id.btn_show_dateTimePicker);
        mAddReminder = findViewById(R.id.btn_add_reminder);

        mShowDateTimeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(CLASS_TAG, "Button Clicked");
                isEvent = true;
                showDatePickerDialog();
            }
        });

        mAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(CLASS_TAG, "Add Reminder Clicked");
                isEvent = false;
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar now = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                MainActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show(getFragmentManager(), "Date Picker");

        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                year = 0;
                month = -1;
                day = 0;
            }
        });
    }

    private void showTimePickerDialog(){
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(MainActivity.this,
                now.get(Calendar.HOUR),
                now.get(Calendar.MINUTE),
                true);

        timePickerDialog.show(getFragmentManager(), "Time Picker");

        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showDatePickerDialog();
            }
        });
    }



    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        Log.d(CLASS_TAG, "Selected Date is : " + date);
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
        showTimePickerDialog();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = "You picked the following time: "+hourOfDay+"h"+minute+"m"+second;
        Log.d(CLASS_TAG, "Selected Date is : " + time);
        this.hour = hourOfDay;
        this.minute = minute;
        if(isEvent) {
            setAlarm();
        }
        else {
            setReminder();
        }
    }

    private void setReminder(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(this.year, this.month, this.day, this.hour, this.minute, 0);
        timeSelected = calendar;
        Intent intent1 = new Intent(MainActivity.this, AlarmReceiver.class);
        intent1.putExtra("LoanName", "Title from Byaj");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(MainActivity.this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void setAlarm(){
        Log.d(CLASS_TAG, "Setting Alarm");
        Calendar cal = Calendar.getInstance();
        cal.set(this.year, this.month, this.day, this.hour, this.minute, 0);
        timeSelected = cal;
        setAlarm(cal);
    }

    private void setAlarm(Calendar target){

        boolean result = checkCalendarPermission();

        if (result){
            writeCalendarEvent(target);
        }
        else {
            Toast.makeText(MainActivity.this, "Your permissions are required to create reminder in your calendar", Toast.LENGTH_LONG).show();
        }
    }

    private void writeCalendarEvent(Calendar target) {
        ContentResolver contentResolver = getContentResolver();
        final ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.CALENDAR_ID, 1);
        event.put(CalendarContract.Events.TITLE, "title");
        event.put(CalendarContract.Events.DESCRIPTION, "description");
        event.put(CalendarContract.Events.EVENT_LOCATION, "location");
        event.put(CalendarContract.Events.DTSTART, target.getTimeInMillis());//startTimeMillis
        event.put(CalendarContract.Events.DTEND, target.getTimeInMillis() + (30 * 60 * 1000));//endTimeMillis
        event.put(CalendarContract.Events.ALL_DAY, 0);
        event.put(CalendarContract.Events.HAS_ALARM, 1);
        String timeZone = TimeZone.getDefault().getID();
        event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone);
        Uri baseUri;
        if (Build.VERSION.SDK_INT >= 8) {
            baseUri = CalendarContract.Events.CONTENT_URI;
        } else {
            baseUri = Uri.parse("content://calendar/events");
        }

        final Uri uri = contentResolver.insert(baseUri, event);
        int dbId = Integer.parseInt(uri.getLastPathSegment());

        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.EVENT_ID, dbId);
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALARM);
        reminders.put(CalendarContract.Reminders.MINUTES, 2);

        final Uri reminder = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminders);

        int added = Integer.parseInt(reminder.getLastPathSegment());

        if(added > 0) {
            Toast.makeText(getApplicationContext(), "Reminder Created", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Failed to create Reminder", Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkCalendarPermission(){
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.KITKAT){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) MainActivity.this, Manifest.permission.WRITE_CALENDAR)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Calendar Permission Required");
                    alertBuilder.setMessage("Write calendar permission is necessary to create reminders in your calendar!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) MainActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity)MainActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_CALENDAR:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (timeSelected != null) {
                        writeCalendarEvent(timeSelected);
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Permission granted. Please click the button to create a reminder in your Calendar", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //code for deny
                    Toast.makeText(MainActivity.this, "Your permissions are required to create reminder in your calendar", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
