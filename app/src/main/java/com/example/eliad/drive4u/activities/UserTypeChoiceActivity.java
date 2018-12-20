package com.example.eliad.drive4u.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.eliad.drive4u.R;

public class UserTypeChoiceActivity extends AppCompatActivity {

    // Tag for the Log
    private final static String TAG = "UserTypeChoiceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_choice);
    }

    public void studentRegistration(View view) {
        Log.d(TAG, "in studentRegistration");
        finish();
        Intent intent = new Intent(this, StudentRegistrationActivity.class);
        startActivity(intent);
    }

    public void teacherRegistration(View view) {
        Log.d(TAG, "in teacherRegistration");
        finish();
        Intent intent = new Intent(this, TeacherRegistrationActivity.class);
        startActivity(intent);
    }
}