package com.example.eliad.drive4u.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.example.eliad.drive4u.adapters.TeacherSearchAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

public class StudentSearchTeacherActivity extends AppCompatActivity {
    // Tag for the Log
    private static final String TAG = StudentSearchTeacherActivity.class.getName();

    // Intent for Parcelables
    private Intent parcelablesIntent;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private DocumentReference mStudentDoc;
    private CollectionReference mTeachersDb;

    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Student mStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_search_teacher);

        Log.d(TAG, "get current firebase user");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;

        parcelablesIntent = getIntent();

        mStudent = parcelablesIntent.getParcelableExtra("Student");

        db = FirebaseFirestore.getInstance();

        mStudentDoc = db.collection("Students").document(mStudent.getID());
        mTeachersDb = db.collection("Teachers");

        initializeRecyclerView();

        presentAllTeachers();
    }

    private void presentAllTeachers() {
        Log.d(TAG, "in presentAllTeachers");
        mTeachersDb
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            LinkedList<Teacher> teachers = new LinkedList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Teacher teacher = document.toObject(Teacher.class);
                                Log.d(TAG, "presenting teacher by email: " + teacher.getEmail());
                                teachers.addLast(teacher);
                            }
                            mAdapter = new TeacherSearchAdapter(teachers, mStudent, mStudentDoc,
                                    mTeachersDb);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void initializeRecyclerView() {
        Log.d(TAG, "in initializeRecyclerView");
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewTeachers);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }
}
