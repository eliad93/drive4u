package com.base.eliad.drive4u.activities.student_activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.adapters.TeacherSearchAdapter;
import com.base.eliad.drive4u.base_activities.StudentBaseActivity;
import com.base.eliad.drive4u.models.Student;
import com.base.eliad.drive4u.models.Teacher;
import com.base.eliad.drive4u.student_ui.StudentSearchTeacherFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

public class StudentSearchTeacherActivity extends StudentBaseActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, TeacherSearchAdapter.OnItemClickListener {
    // Tag for the Log
    private static final String TAG = StudentSearchTeacherActivity.class.getName();
    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    // widgets
    private Spinner mSortSpinner;
    private Spinner mFilterSpinner;
    private Switch mSortSwitch;
    private EditText mFilterSelectedEditText;
    private TextView mSortSelectedTextView;
    private TextView mFilterSelectedTextView;
    private Button mApplyButton;
    private TextView textViewNoTeachers;

    private String mSortSelectedStr;
    private String mFilterSelectedStr;
    private String mFilterSelectedValueStr;
    // models
    private LinkedList<Teacher> teachers = new LinkedList<>();
    private LinkedList<Teacher> presentedTeachers = new LinkedList<>();
    private Teacher currentTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in onCreate");
        setContentView(R.layout.activity_student_search_teacher);

        initWidgets();
        initRecyclerView();
        presetAllTeachers();
    }

    private void presetAllTeachers() {
        Log.d(TAG, "in presentAllTeachers");
        db.collection(getString(R.string.DB_Teachers))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshots = task.getResult();
                            if(snapshots != null){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Teacher teacher = document.toObject(Teacher.class);
                                    Log.d(TAG, "presenting teacher by email: " + teacher.getEmail());
                                    teachers.addLast(teacher);
                                }
                                if (teachers.size() == 0) {
                                    textViewNoTeachers.setVisibility(View.VISIBLE);
                                } else {
                                    presentedTeachers.addAll(teachers);
                                    mAdapter = new TeacherSearchAdapter(presentedTeachers,
                                            StudentSearchTeacherActivity.this);
                                    ((TeacherSearchAdapter) mAdapter)
                                            .setOnItemClickListener(
                                                    StudentSearchTeacherActivity.this);
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                            }
                        }
                        else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void initWidgets() {
        Log.d(TAG, "in initWidgets");
        textViewNoTeachers = findViewById(R.id.textViewStudentSearchTeacherNoTeachers);
        textViewNoTeachers.setVisibility(View.GONE);
        initSpinners();
        initRecyclerView();
    }

    private void initRecyclerView() {
        Log.d(TAG, "in initRecyclerView");
        mRecyclerView = findViewById(R.id.recyclerViewTeachers);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void initSpinners() {
        Log.d(TAG, "in initSpinners");
        mSortSelectedTextView =   findViewById(R.id.textViewStudentSearchTeacherSort);
        mFilterSelectedTextView = findViewById(R.id.textViewStudentSearchTeacherFilter);
        mFilterSelectedEditText = findViewById(R.id.editTextStudentSearchTeacherFilter);
        mFilterSpinner =          findViewById(R.id.spinnerStudentSearchTeacherFilter);
        mSortSpinner =            findViewById(R.id.spinnerStudentSearchTeacherSort);
        mApplyButton =           findViewById(R.id.buttonStudentSearchTeacherApply);
        mSortSwitch =             findViewById(R.id.switchStudentSearchTeacherSortOrder);
        //sort spinner
        ArrayAdapter sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.student_search_teacher_sort_categories,
                android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortSpinner.setAdapter(sortAdapter);
        mSortSpinner.setOnItemSelectedListener(this);
        //filter spinner
        ArrayAdapter filterAdapter = ArrayAdapter.createFromResource(this,
                R.array.student_search_teacher_filter_categories,
                android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFilterSpinner.setAdapter(filterAdapter);
        mFilterSpinner.setOnItemSelectedListener(this);

        mApplyButton.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "in onItemClick");
        currentTeacher = presentedTeachers.get(position);
        // todo: START student choose teacher activity
    }
}
