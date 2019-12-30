package com.example.wemeet.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wemeet.R;
import com.example.wemeet.adapters.RegistrationAdapter;
import com.example.wemeet.classes.Event;
import com.example.wemeet.classes.MyAppCompatActivity;
import com.example.wemeet.classes.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EventDetailsActivity extends MyAppCompatActivity {

    private RecyclerView datesRecyclerView;
    private RegistrationAdapter registrationAdapter;
    private HashMap<String, Boolean> dateHashMap;
    private List<Date> dateArrayList;

    private DocumentReference eventRef;
    private DocumentReference userRef;
    private User user;
    private String eventID;

    private TextView eventNameTextView, locationTextView, peopleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        eventID = getIntent().getStringExtra("eventID");
        if(eventID == null) finish();

        datesRecyclerView = findViewById(R.id.datesRecyclerView);
        eventNameTextView = findViewById(R.id.eventNameTextView);
        locationTextView = findViewById(R.id.locationTextView);
        peopleTextView = findViewById(R.id.peopleTextView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateHashMap = new HashMap<>();


        userRef = mDatabase.collection("users").document(mUser.getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                eventRef = mDatabase.collection("events").document(eventID);
                eventRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Event event = documentSnapshot.toObject(Event.class);
                        eventNameTextView.setText(event.getTitle());
                        locationTextView.setText(event.getLocation());
                        peopleTextView.setText(String.valueOf(event.getInvitedCount()));
                        dateArrayList = event.getDateTimeList();
                        if(user.getRegistrations() != null && user.getRegistrations().containsKey(eventID)){
                            for(Date date : user.getRegistrations().get(eventID)) {
                                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                                String strDate = dateFormat.format(date);
                                dateHashMap.put(strDate, true);
                            }
                        }
                        registrationAdapter = new RegistrationAdapter(getApplicationContext(), dateArrayList, dateHashMap);
                        datesRecyclerView.setAdapter(registrationAdapter);

                    }
                });
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        datesRecyclerView.setLayoutManager(linearLayoutManager);
    }

    public void onCloseButtonClick(View view) {
        finish();
    }
    public void onUpdateButtonClick(View view) {
        dateHashMap = registrationAdapter.getDateHashMap();
        List<Date> userRegistrations = new ArrayList<>();

        for(Date date : dateArrayList) {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            String strDate = dateFormat.format(date);
            if(dateHashMap.get(strDate)){
                userRegistrations.add(date);
            }
        }
        HashMap<String, List<Date>> registrations = user.getRegistrations();
        if (registrations == null) registrations = new HashMap<>();
        registrations.put(eventID, userRegistrations);
        user.setRegistrations(registrations);
        userRef.set(user);

        for(int i = 0; i < dateArrayList.size(); i++){
            try {
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                final String strDate = dateFormat.format(dateArrayList.get(i));
                if(dateHashMap.get(strDate)){
                    final int cnt = i;
                    eventRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Event event = documentSnapshot.toObject(Event.class);
                            HashMap<String, List<DocumentReference>> registrations = event.getRegistrations();
                            if (registrations == null) registrations = new HashMap<>();
                            if(registrations.get(strDate) == null) registrations.put(strDate, new ArrayList<DocumentReference>());
                            if(!registrations.get(strDate).contains(userRef)) registrations.get(strDate).add(userRef);
                            event.setRegistrations(registrations);
                            eventRef.set(event);

                        }
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Toast.makeText(getApplicationContext(), "Registered", Toast.LENGTH_SHORT).show();
    }

}