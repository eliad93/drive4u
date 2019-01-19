package com.example.eliad.drive4u.TeacherUI;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Teacher;

/**
 * A simple {@link TeacherBaseFragment} subclass.
 * Use the {@link TeacherConnectionRequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherConnectionRequestsFragment extends TeacherBaseFragment {
    // Tag for the Log
    private static final String TAG = TeacherConnectionRequestsFragment.class.getName();


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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_connection_requests,
                container, false);
        return view;
    }

}
