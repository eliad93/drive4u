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
import android.widget.Toast;

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

    // Intent for Parcelables
    private Intent parcelablesIntent;

    // the user
    private Student mStudent;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;

    // widgets and recycler view items
    private TextView textViewLessonsCompleted;
    private TextView textViewBalance;
    private TextView textViewNoLessons;

    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        parcelablesIntent = getIntent();

        mStudent = parcelablesIntent.getParcelableExtra("Student");

        // init widgets
        textViewBalance = findViewById(R.id.textViewBalance);
        textViewLessonsCompleted = findViewById(R.id.textViewLessonsCompleted);
        textViewNoLessons = findViewById(R.id.textViewNoLessons);

        String text = Integer.toString(mStudent.getBalance());
        textViewBalance.setText(text);
        text = Integer.toString(mStudent.getNumberOfLessons());  // TODO: fix to the correct number
        textViewLessonsCompleted.setText(text);

        Log.d(TAG, "get current firebase user");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;

        db = FirebaseFirestore.getInstance();

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
            intent.putExtra("Student", mStudent);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.searchTeacher){
            if(mStudent.getTeacherId() == null){
                Toast.makeText(this, "Choose teacher first!", Toast.LENGTH_SHORT);
                Intent intent = new Intent(this, StudentSearchTeacherActivity.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(this, StudentSearchTeacherActivity.class);
                startActivity(intent);
            }
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
        startActivity(new Intent(getBaseContext(), LoginActivity.class));
    }

    private void presentNextLessons() {
        Log.d(TAG, "in presentNextLessons");
        db.collection(getString(R.string.DB_Lessons))
                .whereEqualTo(getString(R.string.DB_Student), mStudent.getID())
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
                            if (lessons.size() > 0) {
                                mAdapter = new LessonsAdapter(lessons);
                                mRecyclerView.setAdapter(mAdapter);
                            } else {
                                Log.d(TAG, "Student " + mStudent.getEmail() + " has no lessons to present");
                                textViewNoLessons.setText(R.string.you_have_no_lessons);
                            }
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
}
