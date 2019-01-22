package com.example.eliad.drive4u.student_ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eliad.drive4u.R;

import org.jetbrains.annotations.Contract;

public class StudentSummaryFragment extends StudentBaseFragment {


    public StudentSummaryFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Contract(" -> new")
    public static StudentSummaryFragment newInstance() {
        return new StudentSummaryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_summary, container, false);
    }

}
