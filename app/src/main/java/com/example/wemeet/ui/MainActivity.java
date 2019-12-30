package com.example.wemeet.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wemeet.R;
import com.example.wemeet.adapters.EventsAdapter;
import com.example.wemeet.classes.Event;
import com.example.wemeet.classes.MyAppCompatActivity;
import com.example.wemeet.classes.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MyAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView eventRecyclerView;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private DrawerLayout drawerLayout;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        eventRecyclerView = findViewById(R.id.eventRecyclerView);
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getApplicationContext(), AddEventActivity.class);
                startActivity(intent);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);


        final ArrayList<Event> events = new ArrayList<>();

        final EventsAdapter eventsAdapter = new EventsAdapter(getApplicationContext(), events);
        eventRecyclerView.setAdapter(eventsAdapter);

        final DocumentReference userRef = mDatabase.collection("users").document(mUser.getUid());
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                user = documentSnapshot.toObject(User.class);


                TextView nameTextView = navigationView.getHeaderView(0).findViewById(R.id.nameTextView);
                TextView emailTextView = navigationView.getHeaderView(0).findViewById(R.id.emailTextView);
                de.hdodenhof.circleimageview.CircleImageView profileImageView = navigationView.getHeaderView(0).findViewById(R.id.profileImageView);

                nameTextView.setText(user.getDisplayName());
                emailTextView.setText(user.getEmail());
                Glide.with(profileImageView)
                        .load(user.getProfilePictureURL())
                        .into(profileImageView);

                List<DocumentReference> searchList = new ArrayList<>();
                searchList.add(userRef);
                if(user.getFriendList() != null && user.getFriendList().size() > 0) {
                    searchList.addAll(user.getFriendList());
                }
                mDatabase.collection("events")
                        .whereIn("owner", searchList)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                events.clear();
                                if(queryDocumentSnapshots != null){
                                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        final Event event = documentSnapshot.toObject(Event.class);
                                        events.add(event);
                                        eventsAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eventRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_logout){
            DocumentReference ref = mDatabase.collection("users").document(mUser.getUid());
            ref.update("token", "").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mAuth.signOut();
                    Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_events:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_search:
                Intent friendsIntent = new Intent(getApplicationContext(), FriendFinderActivity.class);
                startActivity(friendsIntent);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_settings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
