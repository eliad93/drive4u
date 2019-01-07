package com.example.eliad.drive4u;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eliad.drive4u.models.Teacher;


public class TeacherHomeScheduleFragment extends Fragment {

    public TeacherHomeScheduleFragment() {
        // Required empty public constructor
    }

    public static TeacherHomeScheduleFragment newInstance(Teacher teacher) {
        TeacherHomeScheduleFragment fragment = new TeacherHomeScheduleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_home_schedule, container, false);
    }

}
