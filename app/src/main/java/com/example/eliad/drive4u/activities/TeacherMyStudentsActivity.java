package com.example.eliad.drive4u.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.StudentPluralInfoAdapter;
import com.example.eliad.drive4u.base_activities.TeacherBaseActivity;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

public class TeacherMyStudentsActivity extends TeacherBaseActivity {

    private static final String TAG = TeacherMyStudentsActivity.class.getName();

    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_my_students);

        initializeRecyclerView();
        presentStudents();
    }

    private void presentStudents() {
        Log.d(TAG, "presentStudents");
        db.collection(getString(R.string.DB_Students))
                .whereEqualTo("teacherId", mTeacher.getID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "presentStudents.onComplete");
                        if (task.isSuccessful()) {
                            LinkedList<Student> students = new LinkedList<>();
                            Log.d(TAG, "presentStudents.onComplete with result size " + task.getResult().size());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Student student = document.toObject(Student.class);
                                Log.d(TAG, "current student by email: " + student.getEmail());
                                students.addLast(student);
                            }
                            if (students.size() > 0) {
                                mAdapter = new StudentPluralInfoAdapter(students, mTeacher);
                                mRecyclerView.setAdapter(mAdapter);
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
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }
}
