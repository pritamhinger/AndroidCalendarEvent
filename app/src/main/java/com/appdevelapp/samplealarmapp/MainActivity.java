package com.appdevelapp.samplealarmapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String CLASS_TAG = MainActivity.class.getSimpleName();

    Button mshowDateTimeDialogButton;

    int year;
    int month;
    int day;
    int hour;
    int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mshowDateTimeDialogButton = findViewById(R.id.btn_show_dateTimePicker);
        mshowDateTimeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(CLASS_TAG, "Button Clicked");
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
        setAlarm();
    }

    private void setAlarm(){
        Log.d(CLASS_TAG, "Setting Alarm");
    }
}
