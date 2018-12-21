package com.example.eliad.drive4u.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.LessonsAdapter;
import com.example.eliad.drive4u.models.Lesson;
import com.example.eliad.drive4u.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

public class StudentHomeActivity extends AppCompatActivity {
    // Tag for the Log
    private static final String TAG = StudentHomeActivity.class.getName();

    // the user
    private Student student;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;

    // widgets and recycler view items
    private TextView textViewLessonsCompleted;
    private TextView textViewBalance;

    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        Log.d(TAG, "in onCreate");

        // init widgets
        textViewBalance = findViewById(R.id.textViewBalance);
        textViewLessonsCompleted = findViewById(R.id.textViewLessonsCompleted);

        Log.d(TAG, "get current firebase user");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;

        db = FirebaseFirestore.getInstance();

        initUser();

        initLessonsRecyclerView();

        presentNextLessons();
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
        Log.d(TAG, "logoutUser");
        FirebaseAuth.getInstance().signOut();
    }

    private void presentNextLessons() {
        Log.d(TAG, "in presentNextLessons");
        db.collection(getString(R.string.DB_Lessons))
                .whereEqualTo(getString(R.string.DB_Student), student.getID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            LinkedList<Lesson> lessons = new LinkedList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "received document " + document.getId());
                                Lesson lesson = document.toObject(Lesson.class);
                                lessons.addLast(lesson);
                            }
                            mAdapter = new LessonsAdapter(lessons);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void initLessonsRecyclerView() {
        Log.d(TAG, "in initLessonsRecyclerView");
        mRecyclerView = findViewById(R.id.recyclerViewNextLessons);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void initUser() {
        String uId = mUser.getUid();
        DocumentReference docRef = db.collection(getString(R.string.DB_Students)).document(uId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        student = document.toObject(Student.class);
                        textViewBalance.setText(String.format("%d", student.getBalance()));
                        textViewLessonsCompleted.setText(String.format("%d", student.getNumberOfLessons()));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
