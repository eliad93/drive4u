package com.example.eliad.drive4u.teacher_ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Teacher;


public class TeacherSummaryFragment extends TeacherBaseFragment {


    public TeacherSummaryFragment() {
        // Required empty public constructor
    }

    public static TeacherSummaryFragment newInstance(Teacher teacher) {
        TeacherSummaryFragment fragment = new TeacherSummaryFragment();
        Bundle args = newInstanceBaseArgs(teacher);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_summary, container, false);
    }

}
