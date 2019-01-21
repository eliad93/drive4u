package com.example.eliad.drive4u.student_ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Student;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentSummaryFragment extends StudentBaseFragment {


    public StudentSummaryFragment() {
        // Required empty public constructor
    }

    public static StudentSummaryFragment newInstance(Student student) {
        StudentSummaryFragment fragment = new StudentSummaryFragment();
        Bundle args = newInstanceBaseArgs(student);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_summary, container, false);
    }

}
