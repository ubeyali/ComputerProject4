package com.example.wemeet.ui;

import android.os.Bundle;
import android.widget.ExpandableListView;

import com.example.wemeet.R;
import com.example.wemeet.classes.Event;
import com.example.wemeet.classes.ExpandableListAdapter;
import com.example.wemeet.classes.MyAppCompatActivity;
import com.example.wemeet.classes.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;

public class EventOwnerActivity extends MyAppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    Event event;
    String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_owner);

        eventID = getIntent().getStringExtra("eventID");
        if(eventID == null) finish();

        expListView = findViewById(R.id.expandableListView);

        final DocumentReference eventRef = mDatabase.collection("events").document(eventID);
        eventRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                event = documentSnapshot.toObject(Event.class);
                prepareListData();
            }
        });

    }


    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        for(Date date : event.getDateTimeList()){
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            final String header = dateFormat.format(date);
            listDataHeader.add(header);
            if(event.getRegistrations() != null) {
                List<DocumentReference> userRefs = event.getRegistrations().get(header);
                if(userRefs != null){
                    for(DocumentReference userRef : userRefs){
                        final List<String> names = new ArrayList<>();
                        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user = documentSnapshot.toObject(User.class);
                                names.add(user.getDisplayName());
                                listDataChild.put(header, names);
                                listAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            } else {
                ArrayList<String> children = new ArrayList<>();
                children.add("No registrations");
                listDataChild.put(header, children);
            }

        }

        listAdapter = new ExpandableListAdapter(getApplicationContext(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

    }

}
