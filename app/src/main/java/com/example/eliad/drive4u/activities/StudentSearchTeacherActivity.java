package com.example.eliad.drive4u.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.base_activities.StudentBaseActivity;
import com.example.eliad.drive4u.fragments.ChooseTeacherFragment;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.example.eliad.drive4u.adapters.TeacherSearchAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

public class StudentSearchTeacherActivity extends StudentBaseActivity
        implements TeacherSearchAdapter.OnItemClickListener {
    // Tag for the Log
    private static final String TAG = StudentSearchTeacherActivity.class.getName();
    // Firebase
    private DocumentReference mStudentDoc;
    private CollectionReference mTeachersDb;
    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    // models
    private LinkedList<Teacher> teachers = new LinkedList<>();
    // fragments
    private ChooseTeacherFragment chooseTeacherFragment;

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
        // init fragments
        initChooseTeacherFragment();
        // populate recycler view
        presentAllTeachers();
    }

    private void initChooseTeacherFragment() {
        chooseTeacherFragment = new ChooseTeacherFragment();
        chooseTeacherFragment.setArguments(createArgsForFragment());
    }

    private void presentAllTeachers() {
        Log.d(TAG, "in presentAllTeachers");
        mTeachersDb
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Teacher teacher = document.toObject(Teacher.class);
                                Log.d(TAG, "presenting teacher by email: " + teacher.getEmail());
                                teachers.addLast(teacher);
                            }
                            mAdapter = new TeacherSearchAdapter(teachers,
                                    StudentSearchTeacherActivity.this);
                            ((TeacherSearchAdapter) mAdapter)
                                    .setOnItemClickListener(StudentSearchTeacherActivity.this);
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

    private Bundle createArgsForFragment() {
        Log.d(TAG, "in createArgsForFragment");
        Bundle args = new Bundle();
        if(!mStudent.hasTeacher()){
            args.putString("message", "connection request sent.");
        } else {
            args.putString("message", "Already has a teacher, disconnect first.");
        }
        return args;
    }

    private void studentUpdateConnected(final Teacher teacher) {
        Log.d(TAG, "in studentUpdateConnected");
        mStudentDoc.update("teacherId", teacher.getID())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "update teacher id in student success");
                            mStudent.setTeacherId(teacher.getID());
                            // must call it here because it is asynchronous
                            initChooseTeacherFragment();
                        } else {
                            Log.d(TAG, "update teacher id in student failed");
                        }
                    }
                });
        mStudentDoc.update("balance", 0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "update balance in student success");
                            mStudent.setBalance(0);
                        } else {
                            Log.d(TAG, "update balance in student success");
                        }
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "in onItemClick");

        chooseTeacherFragment.show(getSupportFragmentManager(),"student choose teacher");
        if(!mStudent.hasTeacher()){
            final Teacher teacher = teachers.get(position);
            mTeachersDb.document(teacher.getID())
                    .update("students", FieldValue.arrayUnion(mStudent.getID()))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "update students in teacher success");
                                updateDataAfterTeacherChosen(teacher);
                            } else {
                                Log.d(TAG, "update students in teacher failed");
                            }
                        }
                    });
        } else {
            Log.d(TAG, "already has teacher");
        }
    }

    private void updateDataAfterTeacherChosen(Teacher teacher) {
        teacher.addStudent(mStudent.getID());
        studentUpdateConnected(teacher);
    }
}
