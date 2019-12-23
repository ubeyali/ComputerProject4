package com.example.wemeet.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.wemeet.R;
import com.example.wemeet.adapters.EventsAdapter;
import com.example.wemeet.adapters.UsersAdapter;
import com.example.wemeet.classes.Event;
import com.example.wemeet.classes.MyAppCompatActivity;
import com.example.wemeet.classes.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FriendFinderActivity extends MyAppCompatActivity {

    User user;
    ArrayList<User> users;
    DocumentReference userRef;

    RecyclerView usersRecyclerView;
    EditText displayNameEditText;

    UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_finder);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        displayNameEditText = findViewById(R.id.displayNameEditText);

        users = new ArrayList<>();

        userRef = mDatabase.collection("users").document(mUser.getUid());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        usersRecyclerView.setLayoutManager(linearLayoutManager);
    }

    public void onSearchButtonClick(View view) {
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);

                usersAdapter = new UsersAdapter(getApplicationContext(), users, user);
                usersRecyclerView.setAdapter(usersAdapter);

                mDatabase.collection("users")
                        .whereEqualTo("displayName", displayNameEditText.getText().toString())
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        users.clear();
                        if(queryDocumentSnapshots != null){
                            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                final User user = documentSnapshot.toObject(User.class);
                                users.add(user);
                                usersAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                });

            }
        });
    }
}
