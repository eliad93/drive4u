package com.base.eliad.drive4u.activities.teacher_activities;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.adapters.TeacherConnectionRequestsAdapter;
import com.base.eliad.drive4u.base_activities.TeacherBaseNavigationActivity;
import com.base.eliad.drive4u.models.Student;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.LinkedList;

public class TeacherConnectionRequestsActivity extends TeacherBaseNavigationActivity
        implements AdapterView.OnItemSelectedListener, View.OnClickListener,
        TeacherConnectionRequestsAdapter.OnRequestClickListener {

    // Tag for the Log
    private static final String TAG = TeacherConnectionRequestsActivity.class.getName();
    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    // fragment related items
    private FragmentManager fragmentManager;
    // models
    private LinkedList<Student> studentsRequests = new LinkedList<>();
    // widgets
    private TextView textViewNoRequests;
    private Spinner mSortSpinner;
    private Spinner mFilterSpinner;
    private Switch mSortSwitch;
    private EditText mFilterSelectedEditText;
    private TextView mSortSelectedTextView;
    private TextView mFilterSelectedTextView;
    private Button mApplyButton;

    private String mSortSelectedStr;
    private String mFilterSelectedStr;
    private String mFilterSelectedValueStr;
    private LinkedList<Student> presentedRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_connection_requests);

        initWidgets();
        presentAllRequests();
    }

    private void initWidgets() {
        textViewNoRequests = findViewById(R.id.textViewTeacherConnectionRequestsNoRequests);
        textViewNoRequests.setVisibility(View.GONE);
        initSpinners();
        initRecyclerView();
    }

    private void initSpinners() {
        mSortSelectedTextView = findViewById(R.id.textViewTeacherConnectionRequestsSort);
        mFilterSelectedTextView = findViewById(R.id.textViewTeacherConnectionRequestsFilter);
        mFilterSelectedEditText = findViewById(R.id.editTextTeacherConnectionRequestsFilter);
        mFilterSpinner = findViewById(R.id.spinnerTeacherConnectionRequestsFilter);
        mSortSpinner = findViewById(R.id.spinnerTeacherConnectionRequestsSort);
        mApplyButton = findViewById(R.id.buttonTeacherConnectionRequestsApply);
        mSortSwitch = findViewById(R.id.switchStudentTeacherConnectionRequestsSortOrder);
        //sort spinner
        ArrayAdapter sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.teacher_connection_requests_sort_categories,
                android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortSpinner.setAdapter(sortAdapter);
        mSortSpinner.setOnItemSelectedListener(this);
        //filter spinner
        ArrayAdapter filterAdapter = ArrayAdapter.createFromResource(this,
                R.array.teacher_connection_requests_filter_categories,
                android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFilterSpinner.setAdapter(filterAdapter);
        mFilterSpinner.setOnItemSelectedListener(this);

        mApplyButton.setOnClickListener(this);
    }

    private void presentAllRequests() {
        Log.d(TAG, "in presentAllRequests");
        getStudentsDb()
                .whereEqualTo("teacherId", mTeacher.getID())
                .whereEqualTo("request",
                        Student.ConnectionRequestStatus.SENT.getUserMessage())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                    Student student = document.toObject(Student.class);
                    studentsRequests.addLast(student);
                }
                if (studentsRequests.size() == 0) {
                    textViewNoRequests.setVisibility(View.VISIBLE);
                } else {
                    presentedRequests = new LinkedList<>();
                    presentedRequests.addAll(studentsRequests);
                    mAdapter = new TeacherConnectionRequestsAdapter(presentedRequests,
                            TeacherConnectionRequestsActivity.this);
                    ((TeacherConnectionRequestsAdapter) mAdapter)
                            .setOnRequestListener(TeacherConnectionRequestsActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
//        switch(v.getId()){
//            case R.id.buttonStudentSearchTeacherApply:
//                mSortSelectedStr = mSortSelectedTextView.getText().toString();
//                mFilterSelectedStr = mFilterSelectedTextView.getText().toString();
//                mFilterSelectedValueStr = mFilterSelectedEditText.getText().toString();
//
//                if(mSortSelectedStr.isEmpty() && mFilterSelectedStr.isEmpty()
//                        && mFilterSelectedValueStr.isEmpty()) {
//                    Toast.makeText(getContext(),
//                            R.string.filter_sort_empty_error,
//                            Toast.LENGTH_SHORT ).show();
//                }
//                else {
//                    if((!mFilterSelectedValueStr.isEmpty()) && (!mFilterSelectedStr.isEmpty())){
//                        filterTeachers(mFilterSelectedStr,mFilterSelectedValueStr);
//                    }
//                    if(mSortSelectedStr.compareTo
//                            (getString(R.string.all)) != 0){
//                        if(mSortSwitch.isChecked()) {
//                            Collections.sort(presentedTeachers,
//                                    new TeachersCompareDescending(mSortSelectedStr));
//                        }
//                        else {
//                            Collections.sort(presentedTeachers,
//                                    new TeachersCompareAscending(mSortSelectedStr));
//                        }
//                        mAdapter.notifyDataSetChanged();
//                    }
//                }
//                break;
//        }
    }

    private void initRecyclerView() {
        Log.d(TAG, "in initializeRecyclerView");
        mRecyclerView = findViewById(R.id.recyclerViewTeacherConnectionRequests);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onRequestClick(final int position,
                               final Student.ConnectionRequestStatus connectionRequestStatus) {
        final Student selectedStudent = presentedRequests.get(position);
        WriteBatch batch = db.batch();
        batch.update(getStudentDoc(selectedStudent),
                "request", connectionRequestStatus.getUserMessage());
        batch.update(getTeacherDoc(),
                "connectionRequests", FieldValue.arrayRemove(selectedStudent.getID()));
        if(connectionRequestStatus == Student.ConnectionRequestStatus.ACCEPTED){
            batch.update(getTeacherDoc(),
                    "students", FieldValue.arrayUnion(selectedStudent.getID()));
            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    selectedStudent.setRequest(connectionRequestStatus.getUserMessage());
                    presentedRequests.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    mTeacher.removeConnectionRequest(selectedStudent.getID());
                    mTeacher.addStudent(selectedStudent.getID());
                    mAdapter.notifyDataSetChanged();
                    writeTeacherToSharedPreferences();
                }
            });
        } else{
            batch.update(getStudentDoc(selectedStudent),
                    "teacherId", null);
            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    selectedStudent.setRequest(connectionRequestStatus.getUserMessage());
                    selectedStudent.setTeacherId(null);
                    presentedRequests.remove(selectedStudent);
                    mTeacher.removeConnectionRequest(selectedStudent.getID());
                    mAdapter.notifyDataSetChanged();
                    writeTeacherToSharedPreferences();
                }
            });
        }
    }
}
