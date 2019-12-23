package com.example.wemeet.ui;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.wemeet.R;
import com.example.wemeet.classes.Event;
import com.example.wemeet.classes.MyAppCompatActivity;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.annotation.Nullable;

public class AddEventActivity extends MyAppCompatActivity {

    private boolean dateSelected = false;
    private boolean timeSelected = false;

    private int selectedHour, selectedMinute;

    private List<Date> dateList;

    private EditText timeStartEditText, durationEditText, eventNameEditText, dateEditText, locationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        timeStartEditText = findViewById(R.id.timeStartEditText);
        durationEditText = findViewById(R.id.durationEditText);
        eventNameEditText = findViewById(R.id.eventNameEditText);
        dateEditText = findViewById(R.id.dateEditText);
        locationEditText = findViewById(R.id.locationEditText);
    }

    public void onCloseButtonClick(View view) {
        finish();
    }

    public void onCreateButtonClick(View view) {
        if(!dateSelected) {
            Toast.makeText(getApplicationContext(), "Please select dates", Toast.LENGTH_SHORT).show();
        }
        if(!timeSelected) {
            Toast.makeText(getApplicationContext(), "Please select time", Toast.LENGTH_SHORT).show();
        }
        if(locationEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please select a location", Toast.LENGTH_SHORT).show();
        }
        if(eventNameEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please select a title", Toast.LENGTH_SHORT).show();
        }

        List<Date> convertedDates = new ArrayList<>();
        for(Date date : dateList){
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            c.add(Calendar.HOUR, selectedHour);
            c.add(Calendar.MINUTE, selectedMinute);

            date = c.getTime();
            convertedDates.add(date);
        }
        DocumentReference userRef = mDatabase.collection("users").document(mUser.getUid());
        String eventID = UUID.randomUUID().toString();

        Event event = new Event();
        event.setInvitedCount(0);
        event.setLocation(locationEditText.getText().toString());
        event.setTitle(eventNameEditText.getText().toString());
        event.setDateTimeList(convertedDates);
        event.setOwner(userRef);
        event.setEventID(eventID);

        DocumentReference eventRef = mDatabase.collection("events").document(eventID);
        eventRef.set(event);

        Toast.makeText(getApplicationContext(), "Event created", Toast.LENGTH_SHORT).show();
        finish();

    }

    public void onDateClick(View view) {
        dateSelected = true;
        Intent intent = new Intent(getApplicationContext(), DatePickerActivity.class);
        startActivityForResult(intent, 100);
    }

    public void onTimeStartClick(View view) {

        final Calendar c = Calendar.getInstance();
        int sHour = c.get(Calendar.HOUR_OF_DAY);
        int sMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        selectedHour = hourOfDay;
                        selectedMinute = minute;
                        timeSelected = true;
                        if(minute < 10) {
                            timeStartEditText.setText(hourOfDay + ":0" + minute);
                        } else {
                            timeStartEditText.setText(hourOfDay + ":" + minute);
                        }

                    }
                }, sHour, sMinute, true);
        timePickerDialog.show();
    }

    public void onDurationClick(View view) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if(resultCode == Activity.RESULT_OK){
                dateList = DatePickerActivity.DateList;
                dateEditText.setText(String.format("Selected %d days", dateList.size()));
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }
}
