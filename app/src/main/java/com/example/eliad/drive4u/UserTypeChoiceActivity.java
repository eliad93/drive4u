package com.example.eliad.drive4u;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class UserTypeChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_choice);
    }

    public void studentRegistration(View view) {
        finish();
        Intent intent = new Intent(this, StudentRegistrationActivity.class);
        startActivity(intent);
    }

    public void teacherRegistration(View view) {
        finish();
        Intent intent = new Intent(this, TeacherRegistrationActivity.class);
        startActivity(intent);
    }
}
