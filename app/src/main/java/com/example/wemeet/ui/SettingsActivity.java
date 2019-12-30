package com.example.wemeet.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wemeet.R;
import com.example.wemeet.adapters.UsersAdapter;
import com.example.wemeet.classes.ImageHandler;
import com.example.wemeet.classes.MyAppCompatActivity;
import com.example.wemeet.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;

import java.io.File;
import java.io.IOException;

public class SettingsActivity extends MyAppCompatActivity {
    final int RESULT_LOAD_IMAGE = 1;
    DocumentReference userRef;
    ImageView profilePic;
    String Filename;

    TextView nameTextView,emailTextView,numTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        userRef = mDatabase.collection("users").document(mUser.getUid());
        profilePic=findViewById(R.id.profilePicture);
        emailTextView=findViewById(R.id.emailTextView);
        nameTextView=findViewById(R.id.nameTextView);
        numTextView=findViewById(R.id.phoneNumberTextView);



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = mDatabase.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        numTextView.setText(document.getString("phoneNumber"));
                    }
            }
            }
        });
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            nameTextView.setText(name);
            emailTextView.setText(email);
            //numTextView.setText(num);
            Glide.with(profilePic)
                    .load(photoUrl)
                    .into(profilePic);
        }
    }



}