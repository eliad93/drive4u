package com.example.eliad.drive4u;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.example.eliad.drive4u.models.Teacher;

public class TeacherHomeFragment extends TeacherBaseFragment {

    private static final String TAG = TeacherHomeFragment.class.getName();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public TeacherHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public static TeacherHomeFragment newInstance(Teacher t) {
        TeacherHomeFragment fragment = new TeacherHomeFragment();
        Bundle args = getBaseBundle(t);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_teacher_home, container, false);

        if (mTeacher == null && getArguments() == null) {
            Log.d(TAG, "Got a Null teacher and no arguments");
            return rootView;
        }

        if (mTeacher == null) {
            mTeacher = getArguments().getParcelable(ARG_TEACHER);
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
