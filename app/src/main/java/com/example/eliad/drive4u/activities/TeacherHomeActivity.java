package com.example.eliad.drive4u.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.eliad.drive4u.R;
import com.google.firebase.auth.FirebaseAuth;

public class TeacherHomeActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.schedule){
            //teacher schedule activity
        }
        if(item.getItemId() == R.id.students_requests){
            //students requests activity
        }
        if(item.getItemId() == R.id.profile){
            //profile activity
        }
        if(item.getItemId() == R.id.my_students){
            //all students info activity
        }
        if(item.getItemId() == R.id.budget_management){
            //budget management activity
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
