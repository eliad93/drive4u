package com.example.eliad.drive4u.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.StudentHomeLessonsAdapter;
import com.example.eliad.drive4u.adapters.TeacherScheduleAdapter;
import com.example.eliad.drive4u.base_activities.StudentBaseActivity;
import com.example.eliad.drive4u.models.Lesson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

public class StudentHomeActivity extends StudentBaseActivity implements StudentHomeLessonsAdapter.OnItemClickListener{
    // Tag for the Log
    private static final String TAG = StudentHomeActivity.class.getName();

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

        // init widgets
        textViewBalance = findViewById(R.id.textViewBalance);
        textViewLessonsCompleted = findViewById(R.id.textViewLessonsCompleted);
        textViewNoLessons = findViewById(R.id.textViewNoLessons);

        String text = Integer.toString(mStudent.getBalance());
        textViewBalance.setText(text);
        text = Integer.toString(mStudent.getNumberOfLessons());  // TODO: fix to the correct number
        textViewLessonsCompleted.setText(text);

        initLessonsRecyclerView();

        presentNextLessons();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void presentNextLessons() {
        Log.d(TAG, "in presentNextLessons");
        db.collection(getString(R.string.DB_Lessons))
                .whereEqualTo("studentUID", mStudent.getID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            LinkedList<Lesson> lessons = new LinkedList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "received document " + document.getId());
                                Lesson lesson = document.toObject(Lesson.class);
                                if(lesson.getConformationStatus() != Lesson.Status.S_CANCELED){
                                    lessons.addLast(lesson);
                                }
                            }
                            if (lessons.size() > 0) {
                                mAdapter = new StudentHomeLessonsAdapter(StudentHomeActivity.this, lessons);
                                ((StudentHomeLessonsAdapter) mAdapter)
                                        .setOnItemClickListener(StudentHomeActivity.this);
                                mRecyclerView.setAdapter(mAdapter);
                            } else {
                                Log.d(TAG, "Student " + mStudent.getEmail() + " has no lessons to present");
                                textViewNoLessons.setVisibility(View.VISIBLE);
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

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this,String.valueOf(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditButtonClick(int position) {
        Toast.makeText(this,"EDIT "+ String.valueOf(position), Toast.LENGTH_SHORT).show();
    }


}
