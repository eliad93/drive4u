package com.example.eliad.drive4u.teacher_ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;

import java.util.LinkedList;

/**
 * A simple {@link TeacherBaseFragment} subclass.
 * Use the {@link TeacherConnectionRequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherConnectionRequestsFragment extends TeacherBaseFragment {
    // Tag for the Log
    private static final String TAG = TeacherConnectionRequestsFragment.class.getName();
    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    // fragment related items
    private FragmentManager fragmentManager;
    // models
    private LinkedList<Student> studentsRequests = new LinkedList<>();
    // widgets
    private TextView textViewNoRequests;

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
