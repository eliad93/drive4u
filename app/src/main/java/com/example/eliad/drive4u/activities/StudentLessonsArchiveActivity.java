package com.example.eliad.drive4u.activities;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.view.View;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.StudentPastLessonsAdapter;
import com.example.eliad.drive4u.base_activities.StudentBaseActivity;
import com.example.eliad.drive4u.built_in_utils.BorderLineDividerItemDecoration;
import com.example.eliad.drive4u.fragments.StudentArchiveLessonSummaryFragment;
import com.example.eliad.drive4u.models.Lesson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class StudentLessonsArchiveActivity extends StudentBaseActivity
        implements StudentPastLessonsAdapter.StudentPastLessonsItemClickListener,
        StudentArchiveLessonSummaryFragment.StudentLessonSummaryFragmentListener {
    // Tag for the Log
    private static final String TAG = StudentSearchTeacherActivity.class.getName();
    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    // fragment related items
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment fragment;
    // models
    private LinkedList<Lesson> lessons = new LinkedList<>();

    private TextView noLessonsMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in onCreate");
        setContentView(R.layout.activity_student_lessons_archive);

        noLessonsMsg = findViewById(R.id.LessonArchiveNoLessons);
        noLessonsMsg.setVisibility(View.GONE);

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

                            if (lessons.size() == 0) {
                                noLessonsMsg.setVisibility(View.VISIBLE);
                                noLessonsMsg.setText(R.string.you_have_no_lessons);
                            } else {
                                mAdapter = new StudentPastLessonsAdapter(lessons,
                                        StudentLessonsArchiveActivity.this);
                                ((StudentPastLessonsAdapter) mAdapter)
                                        .setOnItemClickListener(StudentLessonsArchiveActivity.this);
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        } else {
                            Log.d(TAG, "student lessons query failed");
                        }
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        Lesson lesson = lessons.get(position);
        Fragment lessonSummaryFragment = StudentArchiveLessonSummaryFragment.newInstance(lesson);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayoutStudentLessonsArchiveActivity,
                lessonSummaryFragment, "Fragment");
        fragmentTransaction.addToBackStack("Fragment");
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
        Fragment fragment = fragmentManager.findFragmentByTag("Fragment");
        resizeFragment(fragment ,0.8, 0.8);
    }

    private void resizeFragment(Fragment fragment, double widthPercent, double heightPercent) {
        if (fragment != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;
            int width = (int) (screenWidth * widthPercent);
            int height = (int) (screenHeight * heightPercent);
            fragment.getView().getLayoutParams().width = width;
            fragment.getView().getLayoutParams().height = height;
        }
    }

    @Override
    public void onFragmentInteraction(Lesson lesson) {
    }
}
