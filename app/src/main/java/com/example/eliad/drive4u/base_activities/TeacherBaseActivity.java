package com.example.eliad.drive4u.base_activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.activities.LoginActivity;
import com.example.eliad.drive4u.activities.TeacherHomeActivity;
import com.example.eliad.drive4u.activities.TeacherMyStudentsActivity;
import com.example.eliad.drive4u.activities.TeacherProfileActivity;
import com.example.eliad.drive4u.activities.TeacherScheduleActivity;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.schedule){
            Intent intent = new Intent(this, TeacherScheduleActivity.class);
            intent.putExtra(TeacherBaseActivity.ARG_TEACHER, mTeacher);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.students_requests){
            //students requests activity
        }
        if(item.getItemId() == R.id.profile){
            //profile activity
            myStartActivity(TeacherProfileActivity.class);
        }
        if(item.getItemId() == R.id.my_students){
            //all students info activity
            myStartActivity(TeacherMyStudentsActivity.class);
        }
//        if(item.getItemId() == R.id.budget_management){
//            //budget management activity
        if (item.getItemId() == R.id.teacher_home) {
            myStartActivity(TeacherHomeActivity.class);
        }
        if(item.getItemId() == R.id.logout){
            logoutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    public void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getBaseContext(), LoginActivity.class));
    }
}
