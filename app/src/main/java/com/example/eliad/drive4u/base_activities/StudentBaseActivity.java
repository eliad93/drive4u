package com.example.eliad.drive4u.base_activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.activities.LoginActivity;
import com.example.eliad.drive4u.activities.StudentHomeActivity;
import com.example.eliad.drive4u.activities.StudentLessonsArchiveActivity;
import com.example.eliad.drive4u.activities.StudentProfileActivity;
import com.example.eliad.drive4u.activities.StudentScheduleLessonActivity;
import com.example.eliad.drive4u.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressLint("Registered")
public class StudentBaseActivity extends AppCompatActivity {
    // Tag for the Log
    private static final String TAG = StudentHomeActivity.class.getName();

    // Bundle arguments
    public static String ARG_STUDENT = Student.class.getName();

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
        Log.d(TAG, "in initDbVariables");
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
        Log.d(TAG, "in onBackPressed");
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
        Log.d(TAG, "in onActivityResult");
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                mStudent = data.getParcelableExtra(ARG_STUDENT);
            }
        }
    }

    protected void myStartActivity(Class<? extends  AppCompatActivity> activity) {
        Log.d(TAG, "in myStartActivity");
        Intent intent = new Intent(this, activity);
        intent.putExtra(ARG_STUDENT, mStudent);
        startActivity(intent);
    }

    protected void myStartActivityForResult(Class<? extends  AppCompatActivity> activity) {
        Log.d(TAG, "in myStartActivityForResult");
        Intent intent = new Intent(this, activity);
        intent.putExtra(ARG_STUDENT, mStudent);
        startActivityForResult(intent, 1);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionMenu");
        getMenuInflater().inflate(R.menu.student_home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        if(item.getItemId() == R.id.scheduleLesson){
            if(mStudent.getTeacherId().equals("")){
                Toast.makeText(this, "You have no teacher. Choose a teacher " +
                                "to schedule a lesson.",
                        Toast.LENGTH_LONG).show();
            }else {
                myStartActivity(StudentScheduleLessonActivity.class);
            }
        }
//        if(item.getItemId() == R.id.searchTeacher){
//        }
        if(item.getItemId() == R.id.pastLessons){
            myStartActivity(StudentLessonsArchiveActivity.class);
        }
        if(item.getItemId() == R.id.profile){
            myStartActivity(StudentProfileActivity.class);
        }

        if (item.getItemId() == R.id.student_home) {
            myStartActivity(StudentHomeActivity.class);
        }

//        if(item.getItemId() == R.id.recentActivities){
//            //Recent activities activity
//        }
//        if(item.getItemId() == R.id.showProgress){
//            //Show progress activity
//        }

        if(item.getItemId() == R.id.logout){
            finish();
            logoutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    public void logoutUser(){
        Log.d(TAG, "logoutUser");
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getBaseContext(), LoginActivity.class));
    }

}
