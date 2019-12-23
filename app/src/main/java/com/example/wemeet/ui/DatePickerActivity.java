package com.example.wemeet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.wemeet.R;
import com.example.wemeet.classes.MyAppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatePickerActivity extends MyAppCompatActivity {

    CalendarPickerView calendarView;

    public static List<Date> DateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        DateList.clear();

        calendarView = findViewById(R.id.calendarView);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        Date today = new Date();

        calendarView.init(today, nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE);
    }

    public void confirmDatesButtonOnClick(View view) {
        DateList.addAll(calendarView.getSelectedDates());
        Intent returnIntent = new Intent();
        if(DateList.size() > 0) {
            setResult(Activity.RESULT_OK,returnIntent);
        } else {
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
        finish();
    }
}
