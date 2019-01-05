package com.example.eliad.drive4u.base_activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.activities.StudentSearchTeacherActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressLint("Registered")
public class RegistrationBaseActivity extends AppCompatActivity {

    // Tag for the Log
    private static final String TAG = StudentSearchTeacherActivity.class.getName();

    protected EditText editTextName;
    protected EditText editTextPhone;
    protected EditText editTextCity;
    protected EditText editTextEmail;
    protected EditText editTextPassword;
    protected EditText editTextPasswordRepeat;
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
