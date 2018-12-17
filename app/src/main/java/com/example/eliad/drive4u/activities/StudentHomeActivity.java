package com.example.eliad.drive4u.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.eliad.drive4u.R;
import com.google.firebase.auth.FirebaseAuth;

public class StudentHomeActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.scheduleLesson){
            Intent intent = new Intent(this, StudentScheduleLessonActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.searchTeacher){
            Intent intent = new Intent(this, StudentSearchTeacherActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.profile){
            //profile activity
        }
        if(item.getItemId() == R.id.recentActivities){
            //Recent activities activity
        }
        if(item.getItemId() == R.id.showProgress){
            //Show progress activity
        }
        if(item.getItemId() == R.id.logout){
            logoutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    public void logoutUser(){
        FirebaseAuth.getInstance().signOut();
    }
}
