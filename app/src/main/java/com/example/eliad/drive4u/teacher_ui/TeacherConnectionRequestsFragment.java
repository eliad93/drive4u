package com.example.eliad.drive4u.teacher_ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;

import java.util.LinkedList;

/**
 * A simple {@link TeacherBaseFragment} subclass.
 * Use the {@link TeacherConnectionRequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherConnectionRequestsFragment extends TeacherBaseFragment
        implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    // Tag for the Log
    private static final String TAG = TeacherConnectionRequestsFragment.class.getName();
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

    public TeacherConnectionRequestsFragment() {
        // Required empty public constructor
    }

    public static TeacherConnectionRequestsFragment newInstance(Teacher teacher) {
        TeacherConnectionRequestsFragment fragment = new TeacherConnectionRequestsFragment();
        Bundle args = newInstanceBaseArgs(teacher);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_connection_requests,
                container, false);
        initWidgets(view);
        return view;
    }

    private void initWidgets(View view) {
        textViewNoRequests = view.findViewById(R.id.textViewTeacherConnectionRequestsNoRequests);
        textViewNoRequests.setVisibility(View.GONE);
        initSpinners(view);
    }

    private void initSpinners(View view) {
        mSortSelectedTextView = view.findViewById(R.id.textViewTeacherConnectionRequestsSort);
        mFilterSelectedTextView = view.findViewById(R.id.textViewTeacherConnectionRequestsFilter);
        mFilterSelectedEditText = view.findViewById(R.id.editTextTeacherConnectionRequestsFilter);
        mFilterSpinner = view.findViewById(R.id.spinnerTeacherConnectionRequestsFilter);
        mSortSpinner = view.findViewById(R.id.spinnerTeacherConnectionRequestsSort);
        mApplyButton = view.findViewById(R.id.buttonTeacherConnectionRequestsApply);
        mSortSwitch = view.findViewById(R.id.switchStudentTeacherConnectionRequestsSortOrder);
        //sort spinner
        Context context = getContext();
        if(context != null){
            ArrayAdapter sortAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.teacher_connection_requests_sort_categories,
                    android.R.layout.simple_spinner_item);
            sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSortSpinner.setAdapter(sortAdapter);
            mSortSpinner.setOnItemSelectedListener(this);
            //filter spinner
            ArrayAdapter filterAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.teacher_connection_requests_filter_categories,
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
//                        TeachersFilter(mFilterSelectedStr,mFilterSelectedValueStr);
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
}
