package com.example.eliad.drive4u.activities;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.StudentPastLessonsAdapter;
import com.example.eliad.drive4u.base_activities.StudentBaseActivity;
import com.example.eliad.drive4u.built_in_utils.BorderLineDividerItemDecoration;
import com.example.eliad.drive4u.fragments.StudentArciveLessonSummaryFragment;
import com.example.eliad.drive4u.models.Lesson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

public class StudentLessonsArchiveActivity extends StudentBaseActivity
        implements StudentPastLessonsAdapter.StudentPastLessonsItemClickListener,
        StudentArciveLessonSummaryFragment.StudentLessonSummaryFragmentListener {
    // Tag for the Log
    private static final String TAG = StudentSearchTeacherActivity.class.getName();
    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    // fragment related items
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    // models
    private LinkedList<Lesson> lessons = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in onCreate");
        setContentView(R.layout.activity_student_lessons_archive);

        initializeFragmentObjects();

        initializeRecyclerView();

        presentAllStudentPastLessons();
    }

    @SuppressLint("CommitTransaction")
    private void initializeFragmentObjects() {
        fragmentManager = getSupportFragmentManager();
    }

    private void initializeRecyclerView() {
        Log.d(TAG, "in initializeRecyclerView");
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewStudentLessonsArchive);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new BorderLineDividerItemDecoration(this));
    }

    private void presentAllStudentPastLessons() {
        Log.d(TAG, "in presentAllStudentPastLessons");
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
                                    StudentLessonsArchiveActivity.this);
                            ((StudentPastLessonsAdapter) mAdapter)
                                    .setOnItemClickListener(StudentLessonsArchiveActivity.this);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d(TAG, "student lessons query failed");
                        }
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        Lesson lesson = lessons.get(position);
        Fragment lessonSummaryFragment = StudentArciveLessonSummaryFragment.newInstance(lesson);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayoutStudentLessonsArchiveActivity, lessonSummaryFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Lesson lesson) {
    }
}
