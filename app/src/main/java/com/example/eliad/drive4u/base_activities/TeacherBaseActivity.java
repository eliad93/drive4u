package com.example.eliad.drive4u.base_activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.eliad.drive4u.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressLint("Registered")
public class TeacherBaseActivity extends AppCompatActivity {
    // Tag for the Log
    private static final String TAG = TeacherBaseActivity.class.getName();

    // key for passing the teacher between activities
    public static final String ARG_TEACHER = "com.android.eliad.base_activities.StudentBaseActivity.teacher_key";
    // Intent for Parcelables
    protected Intent parcelablesIntent;
    // the user
    protected Teacher mTeacher;
    // Firebase
    protected FirebaseAuth mAuth;
    protected FirebaseUser mUser;
    protected FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDbVariables();
        parcelablesIntent = getIntent();
        mTeacher = parcelablesIntent.getParcelableExtra(ARG_TEACHER);
    }

    protected void initDbVariables() {
        Log.d(TAG, "get current firebase user");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;
        db = FirebaseFirestore.getInstance();
    }

    /*
        all teacher activities should send an updated teacher to the prev activity
     */
    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra(ARG_TEACHER, mTeacher);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
    /*
        all teacher activities should update their teacher
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                mTeacher = data.getParcelableExtra(ARG_TEACHER);
            }
        }
    }

    protected void myStartActivity(Class<? extends  AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(ARG_TEACHER, mTeacher);
        startActivity(intent);
    }

    protected void myStartActivityForResult(Class<? extends  AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(ARG_TEACHER, mTeacher);
        startActivityForResult(intent, 1);
    }
}
