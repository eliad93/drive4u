package com.example.eliad.drive4u.student_ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.TeacherSearchAdapter;
import com.example.eliad.drive4u.fragments.StudentChooseTeacherFragment;
import com.example.eliad.drive4u.helpers.ConditionsHelper;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.jetbrains.annotations.Contract;

import java.util.LinkedList;

public class StudentSearchTeacherFragment extends StudentBaseFragment
        implements TeacherSearchAdapter.OnItemClickListener,
        StudentChooseTeacherFragment.PerformUserAction,
        AdapterView.OnItemSelectedListener, View.OnClickListener {
    // Tag for the Log
    private static final String TAG = StudentSearchTeacherFragment.class.getName();
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

    public StudentSearchTeacherFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Contract("_ -> new")
    public static StudentSearchTeacherFragment newInstance(Student student) {
        return new StudentSearchTeacherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_student_search_teacher,
                container, false);
        textViewNoTeachers = view.findViewById(R.id.textViewStudentSearchTeacherNoTeachers);
        textViewNoTeachers.setVisibility(View.GONE);
        // data handling
        initializeSpinners(view);
        initializeRecyclerView(view);
        // populate recycler view
        presentAllTeachers();
        return view;
    }

    private void initializeSpinners(View view) {
        mSortSelectedTextView =   view.findViewById(R.id.textViewStudentSearchTeacherSort);
        mFilterSelectedTextView = view.findViewById(R.id.textViewStudentSearchTeacherFilter);
        mFilterSelectedEditText = view.findViewById(R.id.editTextStudentSearchTeacherFilter);
        mFilterSpinner =          view.findViewById(R.id.spinnerStudentSearchTeacherFilter);
        mSortSpinner =            view.findViewById(R.id.spinnerStudentSearchTeacherSort);
        mApplyButton =           view.findViewById(R.id.buttonStudentSearchTeacherApply);
        mSortSwitch =             view.findViewById(R.id.switchStudentSearchTeacherSortOrder);
        //sort spinner
        Context context = getContext();
        if(context != null){
            ArrayAdapter sortAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.student_search_teacher_sort_categories,
                    android.R.layout.simple_spinner_item);
            sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSortSpinner.setAdapter(sortAdapter);
            mSortSpinner.setOnItemSelectedListener(this);
            //filter spinner
            ArrayAdapter filterAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.student_search_teacher_filter_categories,
                    android.R.layout.simple_spinner_item);
            filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mFilterSpinner.setAdapter(filterAdapter);
            mFilterSpinner.setOnItemSelectedListener(this);

            mApplyButton.setOnClickListener(this);
        } else {
            unexpectedError();
        }
     }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonStudentSearchTeacherApply:
                mSortSelectedStr = mSortSelectedTextView.getText().toString();
                mFilterSelectedStr = mFilterSelectedTextView.getText().toString();
                mFilterSelectedValueStr = mFilterSelectedEditText.getText().toString();

                if(mSortSelectedStr.isEmpty() && mFilterSelectedStr.isEmpty()
                        && mFilterSelectedValueStr.isEmpty()) {
                    Toast.makeText(getContext(),
                            R.string.filter_sort_empty_error,
                            Toast.LENGTH_SHORT ).show();
                }
                else {
                    if((!mFilterSelectedValueStr.isEmpty()) && (!mFilterSelectedStr.isEmpty())){
                        TeachersFilter(mFilterSelectedStr, mFilterSelectedValueStr);
                    }
                    if(mSortSelectedStr.compareTo
                            (getString(R.string.all)) != 0){
                       if(mSortSwitch.isChecked()) {
                           sortTeachers(mSortSelectedStr, ConditionsHelper.Order.DESCENDING);
                       }
                       else {
                           sortTeachers(mSortSelectedStr, ConditionsHelper.Order.ASCENDING);
                       }
                    mAdapter.notifyDataSetChanged();
                }
                }
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private void sortTeachers(String mSortSelectedStr, ConditionsHelper.Order order) {
        switch(mSortSelectedStr){
            case "city":
                Teacher.Sort.name(presentedTeachers, order);
            case "number of students":
                Teacher.Sort.numberOfStudents(presentedTeachers, order);
            case "lesson length":
                Teacher.Sort.lessonLength(presentedTeachers, order);
            case "price":
                Teacher.Sort.price(presentedTeachers, order);
            case "worth":
                Teacher.Sort.worth(presentedTeachers, order);
            default:
                Teacher.Sort.name(presentedTeachers, order);
        }
    }

    @SuppressWarnings("unchecked")
    public void TeachersFilter(String key , String value){
        presentedTeachers.clear();
        presentedTeachers.addAll(teachers);
        switch (key){
            case "city":
                Teacher.Filter.cityEquals(presentedTeachers, value);
                break;
            case "number of students":
                Teacher.Filter.numberOfStudents(presentedTeachers,
                        Integer.valueOf(value));
                break;
            case "gear type":
                Teacher.Filter.gearType(presentedTeachers, value);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch(parent.getId()) {
                case R.id.spinnerStudentSearchTeacherFilter:
                    if(position == 0) {
                        mFilterSelectedTextView.setText("");
                        break;
                    }
                    mFilterSelectedTextView.setText(parent.getItemAtPosition(position).toString());
                    break;
                case R.id.spinnerStudentSearchTeacherSort:
                    if(position == 0) {
                        mSortSelectedTextView.setText("");
                        break;
                    }
                    mSortSelectedTextView.setText(parent.getItemAtPosition(position).toString());
                    break;
            }
        }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void initializeRecyclerView(View view) {
        Log.d(TAG, "in initializeRecyclerView");
        mRecyclerView = view.findViewById(R.id.recyclerViewTeachers);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void presentAllTeachers() {
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
                                    mAdapter = new TeacherSearchAdapter(presentedTeachers, getContext());
                                    ((TeacherSearchAdapter) mAdapter)
                                            .setOnItemClickListener(StudentSearchTeacherFragment.this);
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

    private void studentUpdateConnected() {
        Log.d(TAG, "in studentUpdateConnected");
        mStudent.setTeacherId(currentTeacher.getID());
        mStudent.setBalance(0);
        db.collection(getString(R.string.DB_Student))
                .document(mStudent.getID())
                .update("teacherId", currentTeacher.getID(),
                        "balance", 0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "update teacher id and balance in student success");
                        } else {
                            Log.d(TAG, "update teacher id and balance in student failed");
                        }
                    }
                });
        writeStudentToSharedPreferences();
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "in onItemClick");
        currentTeacher = presentedTeachers.get(position);
        Fragment teacherFragment = StudentChooseTeacherFragment.newInstance(mStudent, currentTeacher);
        startFragmentForResult(teacherFragment);
    }

    private void startFragmentForResult(Fragment fragment) {
        Log.d(TAG, "in startFragmentForResult");
        FragmentManager fm = getFragmentManager();
        assert fm != null;
        fragment.setTargetFragment(this, 0);
        fm.beginTransaction()
                .replace(R.id.frameStudentChooseTeacher, fragment)
                .commit();
    }

    private void updateDataAfterRequest() {
        Log.d(TAG, "in updateDataAfterRequest");
        mStudent.setTeacherId(currentTeacher.getID());
        mStudent.setRequest(Student.ConnectionRequestStatus.SENT.getUserMessage());
        currentTeacher.addConnectionRequest(mStudent.getID());
        writeStudentToSharedPreferences();
        writeStudentTeacherToSharedPreferences(currentTeacher);
        FragmentManager fm = getFragmentManager();
        if(fm != null){
            for(Fragment f: fm.getFragments()){
                if(f instanceof StudentChooseTeacherFragment){
                    fm.beginTransaction().remove(f).commit();
                }
            }
        }
    }

    @Override
    public void performUserAction() {
        Log.d(TAG, "in performUserAction");
        DocumentReference mStudentDoc = getStudentDoc();
        // batch writes together
        WriteBatch batch = db.batch();
        // update student
        batch.update(mStudentDoc, "request",
                Student.ConnectionRequestStatus.SENT.getUserMessage());
        batch.update(mStudentDoc, "teacherId", currentTeacher.getID());
        // update teacher
        DocumentReference teacher = getTeachersDb().document(currentTeacher.getID());
        batch.update(teacher, "connectionRequests",
                FieldValue.arrayUnion(mStudent.getID()));
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "connection request to teacher sent");
                updateDataAfterRequest();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "connection request to teacher failed");
            }
        });
    }
}
