package com.example.eliad.drive4u.base_activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.eliad.drive4u.activities.StudentHomeActivity;
import com.example.eliad.drive4u.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressLint("Registered")
public class StudentBaseActivity extends AppCompatActivity {
    // Tag for the Log
    private static final String TAG = StudentHomeActivity.class.getName();

    // string for passing the student
    public static String ARG_STUDENT = "com.android.eliad.base_activities.StudentBaseActivity.student_key";

    // Intent for Parcelables
    protected Intent parcelablesIntent;

    // the user
    protected Student mStudent;

    // Firebase
    protected FirebaseAuth mAuth;
    protected FirebaseUser mUser;
    protected FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in onCreate");
        initDbVariables();
        parcelablesIntent = getIntent();
        mStudent = parcelablesIntent.getParcelableExtra(ARG_STUDENT);
    }

    protected void initDbVariables() {
        Log.d(TAG, "get current firebase user");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;
        db = FirebaseFirestore.getInstance();
    }

    /*
        all student activities should send an updated student to the prev activity
     */
    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra(ARG_STUDENT, mStudent);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
    /*
        all student activities should update their student
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                mStudent = data.getParcelableExtra(ARG_STUDENT);
            }
        }
    }

    protected void myStartActivity(Class<? extends  AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(ARG_STUDENT, mStudent);
        startActivity(intent);
    }

    protected void myStartActivityForResult(Class<? extends  AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(ARG_STUDENT, mStudent);
        startActivityForResult(intent, 1);
    }

}
