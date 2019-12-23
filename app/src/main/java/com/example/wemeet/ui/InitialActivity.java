package com.example.wemeet.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.wemeet.R;
import com.example.wemeet.classes.MyAppCompatActivity;


public class InitialActivity extends MyAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mUser != null){

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_initial);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void loginButtonOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void registerButtonOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }
}
