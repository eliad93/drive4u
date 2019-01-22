package com.example.eliad.drive4u.teacher_ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eliad.drive4u.R;

import org.jetbrains.annotations.Contract;


public class TeacherSummaryFragment extends TeacherBaseFragment {


    public TeacherSummaryFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Contract(" -> new")
    public static TeacherSummaryFragment newInstance() {
        return new TeacherSummaryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_summary, container, false);
    }

}
