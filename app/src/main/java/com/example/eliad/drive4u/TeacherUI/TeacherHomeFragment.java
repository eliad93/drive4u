package com.example.eliad.drive4u.TeacherUI;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Teacher;

public class TeacherHomeFragment extends Fragment {

    private static final String TAG = TeacherHomeFragment.class.getName();

    public static final String ARG_TEACHER = TAG + ".arg_teacher";

    private Teacher mTeacher;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    public TeacherHomeFragment() {
        // Required empty public constructor
    }


    public static TeacherHomeFragment newInstance(Teacher teacher) {
        TeacherHomeFragment fragment = new TeacherHomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TEACHER, teacher);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            Teacher t = savedInstanceState.getParcelable(ARG_TEACHER);
//            if (t != null) {
//                mTeacher = t;
//            }
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_home, container, false);

        if (mTeacher == null && getArguments() == null) {
            Log.d(TAG, "Got a Null teacher and no arguments");
            return view;
        } else if (mTeacher == null) {
            mTeacher = getArguments().getParcelable(ARG_TEACHER);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_home, container, false);
    }

}
