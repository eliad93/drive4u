package com.example.eliad.drive4u;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
        register(getString(R.string.student));
    }

    public void teacherRegistration(View view) {
        Log.d(TAG, "in studentRegistration");
        register(getString(R.string.teacher));
    }

    private void register(String userType) {
        finish();
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra(getString(R.string.user_home_activity), userType);
        startActivity(intent);
    }
}
