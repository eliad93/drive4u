package com.example.eliad.drive4u.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.StudentPastLessonsAdapter;
import com.example.eliad.drive4u.base_activities.StudentBaseActivity;
import com.example.eliad.drive4u.models.Lesson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

public class StudentPastLessonsActivity extends StudentBaseActivity {
    // Tag for the Log
    private static final String TAG = StudentSearchTeacherActivity.class.getName();
    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    // models
    private LinkedList<Lesson> lessons = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in onCreate");
        setContentView(R.layout.activity_student_past_lessons);

        initializeRecyclerView();

        presentAllStudentPastLessons();
    }

    private void initializeRecyclerView() {
        Log.d(TAG, "in initializeRecyclerView");
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewStudentPastLessons);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void presentAllStudentPastLessons() {
        Log.d(TAG, "in presentAllStudentPastLessons");
        final LinkedList<Lesson> lessons = new LinkedList<>();
        db.collection("lessons")
                .whereEqualTo("studentUID", mStudent.getID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "student lessons query succeeded");
                            for (QueryDocumentSnapshot document : task.getResult()){
                                Lesson lesson = document.toObject(Lesson.class);
                                lessons.addLast(lesson);
                            }
                            mAdapter = new StudentPastLessonsAdapter(lessons,
                                    StudentPastLessonsActivity.this);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d(TAG, "student lessons query failed");
                        }
                    }
                });
    }
}
