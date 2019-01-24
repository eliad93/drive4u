package com.base.eliad.drive4u.activities.student_activities;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.adapters.ArchivedLessonsAdapter;
import com.base.eliad.drive4u.base_activities.StudentBaseActivity;
import com.base.eliad.drive4u.fragments.StudentArchiveLessonSummaryFragment;
import com.base.eliad.drive4u.models.Lesson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

public class StudentLessonsArchiveActivity extends StudentBaseActivity
        implements ArchivedLessonsAdapter.StudentPastLessonsItemClickListener {
    // Tag for the Log
    private static final String TAG = StudentLessonsArchiveActivity.class.getName();
    // arguments
    private static final double LESSON_FRAGMENT_WIDTH_PERCENT = 0.8;
    private static final double LESSON_FRAGMENT_HEIGHT_PERCENT = 0.8;
    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    // fragment related items
    private FragmentManager fragmentManager;
    // models
    private LinkedList<Lesson> lessons = new LinkedList<>();
    // widgets
    private TextView noLessonsMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lessons_archive);
        initWidgets();
        initRecyclerView();
        presentAllStudentPastLessons();
    }

    private void initWidgets() {
        noLessonsMsg = findViewById(R.id.LessonArchiveNoLessons);
        noLessonsMsg.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        Log.d(TAG, "in initRecyclerView");
        mRecyclerView = findViewById(R.id.recyclerViewStudentLessonsArchive);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
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
                            QuerySnapshot snapshots = task.getResult();
                            if(snapshots != null){
                                for (QueryDocumentSnapshot document : snapshots){
                                    Lesson lesson = document.toObject(Lesson.class);
                                    lessons.addLast(lesson);
                                }
                            }
                            if (lessons.size() == 0) {
                                noLessonsMsg.setText(R.string.you_had_no_lessons_yet);
                                noLessonsMsg.setVisibility(View.VISIBLE);
                            } else {
                                mAdapter = new ArchivedLessonsAdapter(lessons,
                                        StudentLessonsArchiveActivity.this);
                                ((ArchivedLessonsAdapter) mAdapter)
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
        Fragment lessonSummaryFragment = StudentArchiveLessonSummaryFragment.newInstance();
        Bundle args = new Bundle();
        args.putParcelable(StudentArchiveLessonSummaryFragment.ARG_LESSON, lesson);
        lessonSummaryFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameStudentLessonsArchive,
                lessonSummaryFragment, "Fragment");
        fragmentTransaction.addToBackStack("Fragment")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        fragmentManager.executePendingTransactions();
        Fragment fragment = fragmentManager.findFragmentByTag("Fragment");
        resizeFragment(fragment, LESSON_FRAGMENT_WIDTH_PERCENT, LESSON_FRAGMENT_HEIGHT_PERCENT);
    }
}
