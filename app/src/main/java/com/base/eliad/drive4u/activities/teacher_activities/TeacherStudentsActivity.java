package com.base.eliad.drive4u.activities.teacher_activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.adapters.StudentPluralInfoAdapter;
import com.base.eliad.drive4u.base_activities.TeacherBaseNavigationActivity;
import com.base.eliad.drive4u.built_in_utils.BorderLineDividerItemDecoration;
import com.base.eliad.drive4u.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

public class TeacherStudentsActivity extends TeacherBaseNavigationActivity {

    public final static String TAG = TeacherStudentsActivity.class.getName();

    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView textViewNoStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_students);

        textViewNoStudents = findViewById(R.id.textView18);
        initializeRecyclerView();
        presentStudents();

    }

    private void presentStudents() {
        Log.d(TAG, "presentStudents");
        textViewNoStudents.setVisibility(View.GONE);
        db.collection(getString(R.string.DB_Students))
                .whereEqualTo("teacherId", mTeacher.getID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "presentStudents.onComplete");
                        if (task.isSuccessful()) {
                            LinkedList<Student> students = new LinkedList<>();
                            QuerySnapshot snapshots = task.getResult();
                            if(snapshots != null){
                                for (QueryDocumentSnapshot document : snapshots) {
                                    Student student = document.toObject(Student.class);
                                    Log.d(TAG, "current student by email: " + student.getEmail());
                                    students.addLast(student);
                                }
                            }
                            if (students.size() > 0) {
                                mAdapter = new StudentPluralInfoAdapter(students, mTeacher, TeacherStudentsActivity.this);
                                mRecyclerView.setAdapter(mAdapter);
                            } else {
                                textViewNoStudents.setVisibility(View.VISIBLE);
                                Log.d(TAG, "This Teacher has no students");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void initializeRecyclerView() {
        Log.d(TAG, "in initializeRecyclerView");
        mRecyclerView         = findViewById(R.id.TeacherMyStudentsRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(TeacherStudentsActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }
}
