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
import com.example.eliad.drive4u.adapters.TeacherHomeLessonsAdapter;
import com.example.eliad.drive4u.base_activities.TeacherBaseActivity;
import com.example.eliad.drive4u.models.Lesson;
import com.example.eliad.drive4u.models.Teacher;
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

public class TeacherHomeActivity extends TeacherBaseActivity {

    // Log
    private static final String TAG = TeacherHomeActivity.class.getName();

    // widgets
    private TextView textViewLessonsRemainingToday;
    private TextView textViewNoLessons;

    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        Log.d(TAG, "onCreate");

        textViewLessonsRemainingToday = findViewById(R.id.textViewLessonsRemainingToday);
        textViewNoLessons = findViewById(R.id.TeacherHomeTextViewNoLessosns);

        textViewLessonsRemainingToday.setText("0"); // TODO

        initLessonsRecyclerView();

        presentNextLessons();
    }

    private void presentNextLessons() {
        Log.d(TAG, "in presentNextLessons");

        db.collection(getString(R.string.DB_Lessons))
                .whereEqualTo("teacherUID", mTeacher.getID())
//                .whereEqualTo("conformationStatus", Lesson.Status.S_CONFIRMED)
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
                            if (lessons.size() > 0 ) {
                                mAdapter = new TeacherHomeLessonsAdapter(lessons);
                                mRecyclerView.setAdapter(mAdapter);
                            } else {
                                Log.d(TAG, "Teacher " + mTeacher.getEmail() + " has no lessons to present");
                                textViewNoLessons.setText(R.string.you_have_no_lessons);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void initLessonsRecyclerView() {
        Log.d(TAG, "in initializeNextLessonsRecyclerView");
        mRecyclerView = findViewById(R.id.recyclerViewTeacherLessons);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }
}
