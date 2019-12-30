package com.example.wemeet.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.wemeet.R;
import com.example.wemeet.adapters.UsersAdapter;
import com.example.wemeet.adapters.UsersShareAdapter;
import com.example.wemeet.classes.MyAppCompatActivity;
import com.example.wemeet.classes.User;
import com.github.tntkhang.gmailsenderlibrary.GMailSender;
import com.github.tntkhang.gmailsenderlibrary.GmailListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ShareActivity extends MyAppCompatActivity {

    User user;
    ArrayList<User> users;
    DocumentReference userRef;
    DocumentReference userRef1;

    UsersShareAdapter mAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        users = new ArrayList<>();

        userRef = mDatabase.collection("users").document(mUser.getUid());

        recyclerView = findViewById(R.id.usersRecyclerView);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                mAdapter = new UsersShareAdapter(getApplicationContext(), users, user);
                recyclerView.setAdapter(mAdapter);

                final List<DocumentReference> friendList = new ArrayList<>();
                if(user.getFriendList() != null && user.getFriendList().size() > 0) {
                    friendList.addAll(user.getFriendList());
                }
             //
                //
                mDatabase.collection(friendList.get(0).get().toString());
                Task<DocumentSnapshot> s=friendList.get(0).get();
                userRef1 = mDatabase.collection(String.valueOf(s)).document(mUser.getUid());

                mDatabase.collection("users")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        users.clear();
                        if(queryDocumentSnapshots != null){
                            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                final User user = documentSnapshot.toObject(User.class);
                                for(int i=0;i<friendList.size();i++){
                                    if(friendList.get(i).getId().contains(user.getUserID()))
                                        users.add(user);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                });

            }
        });
        String message="I would like to invite you to the W'meet Event. Please use this code to join. ";
    }

    public void sendWpMessage(String message, String number){
        number = number.replace("+", "").replace(" ", "");
        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.putExtra("jid", number + "@s.whatsapp.net");
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
    public void sendEmail(String message, String email){
        GMailSender.withAccount("computerproject3030@gmail.com", "BilgisayarProject1")
                .withTitle("W'meet event join code")
                .withBody(message)
                .withSender(getString(R.string.app_name))
                .toEmailAddress(email)
                .withListenner(new GmailListener() {
                    @Override
                    public void sendSuccess() {
                        Toast.makeText(ShareActivity.this, "Email send", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void sendFail(String err) {
                       Toast.makeText(ShareActivity.this, "Fail: " + err, Toast.LENGTH_SHORT).show();
                    }
                })
                .send();
    }
    public void btnDoneonClik(View view) {
        finish();
    }
}
