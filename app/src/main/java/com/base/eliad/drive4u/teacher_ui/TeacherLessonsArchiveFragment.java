package com.base.eliad.drive4u.teacher_ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.adapters.ArchivedLessonsAdapter;
import com.base.eliad.drive4u.models.Lesson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

public class TeacherLessonsArchiveFragment extends TeacherBaseFragment
        implements ArchivedLessonsAdapter.StudentPastLessonsItemClickListener {
    // Tag for the Log
    private static final String TAG = TeacherLessonsArchiveFragment.class.getName();
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

    public TeacherLessonsArchiveFragment() {
        // Required empty public constructor
    }

    public static TeacherLessonsArchiveFragment newInstance() {
        return new TeacherLessonsArchiveFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.lessons_archive, container, false);
        initWidgets(view);
        initFragmentObjects();
        initRecyclerView(view);
        presentAllTeacherPastLessons();
        return view;
    }

    private void initWidgets(@NonNull View view) {
        noLessonsMsg = view.findViewById(R.id.LessonArchiveNoLessons);
        noLessonsMsg.setVisibility(View.GONE);
    }

    private void initFragmentObjects() {
        fragmentManager = getFragmentManager();
    }

    private void initRecyclerView(@NonNull View view) {
        Log.d(TAG, "in initRecyclerView");
        mRecyclerView = view.findViewById(R.id.recyclerViewStudentLessonsArchive);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void presentAllTeacherPastLessons() {
        Log.d(TAG, "in presentAllStudentPastLessons");
        db.collection("lessons")
                .whereEqualTo("teacherUID", mTeacher.getID())
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
                                mAdapter = new ArchivedLessonsAdapter(lessons, getContext());
                                ((ArchivedLessonsAdapter) mAdapter)
                                        .setOnItemClickListener(TeacherLessonsArchiveFragment.this);
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
        Fragment lessonSummaryFragment = TeacherLessonsArchiveFragment.newInstance();
        fragmentManager.beginTransaction().add(R.id.frameStudentLessonsArchive,
                lessonSummaryFragment, "Fragment").addToBackStack("Fragment")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        fragmentManager.executePendingTransactions();
        Fragment fragment = fragmentManager.findFragmentByTag("Fragment");
        resizeFragment(fragment);
    }

    private void resizeFragment(Fragment fragment) {
        if (fragment != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            Activity activity = getActivity();
            View view = fragment.getView();
            if(activity == null || view == null){
                return;
            }
            WindowManager windowManager = activity.getWindowManager();
            if(windowManager == null){
                return;
            }
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;
            int width = (int) (screenWidth * LESSON_FRAGMENT_WIDTH_PERCENT);
            int height = (int) (screenHeight * LESSON_FRAGMENT_HEIGHT_PERCENT);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = width;
            params.height = height;
        }
    }
}
