package com.example.eliad.drive4u.TeacherUI;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Teacher;

public class TeacherChatsFragment extends TeacherBaseFragment {

    public TeacherChatsFragment() {
        // Required empty public constructor
    }

    public static TeacherChatsFragment newInstance(Teacher teacher) {
        TeacherChatsFragment fragment = new TeacherChatsFragment();
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
        View view =  inflater.inflate(R.layout.fragment_chats, container, false);

        return view;
    }

}
