package com.example.eliad.drive4u.teacher_ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Teacher;

public class TeacherHomeFragment extends TeacherBaseFragment {

    private static final String TAG = TeacherHomeFragment.class.getName();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            String msg = "BUG";
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    msg = "Home selected";
                    break;
                case R.id.navigation_dashboard:
                    msg = "Dashboard selected";
                    break;
                case R.id.navigation_notifications:
                    msg = "notifications selected";
                    break;
            }
            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            return true;
        }
    };

    public TeacherHomeFragment() {
        // Required empty public constructor
    }


    public static TeacherHomeFragment newInstance(Teacher teacher) {
        TeacherHomeFragment fragment = new TeacherHomeFragment();
        Bundle args = newInstanceBaseArgs(teacher);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_teacher_home, container, false);

        if (mTeacher == null && getArguments() == null) {
            Log.d(TAG, "Got a Null teacher and no arguments");
            return view;
        } else if (mTeacher == null) {
            mTeacher = getArguments().getParcelable(ARG_TEACHER);
        }

        BottomNavigationView navigation = (BottomNavigationView) view.findViewById(R.id.teacher_home_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // Inflate the layout for this fragment
        return view;
    }

}