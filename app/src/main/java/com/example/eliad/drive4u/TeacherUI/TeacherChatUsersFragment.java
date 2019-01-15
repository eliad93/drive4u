package com.example.eliad.drive4u.TeacherUI;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Teacher;

public class TeacherChatUsersFragment extends TeacherBaseFragment {

    public TeacherChatUsersFragment() {
        // Required empty public constructor
    }

    public static TeacherChatUsersFragment newInstance(Teacher teacher) {
        TeacherChatUsersFragment fragment = new TeacherChatUsersFragment();
        Bundle args = newInstanceBaseArgs(teacher);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_users, container, false);

        return view;
    }

}
