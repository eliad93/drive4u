package com.example.eliad.drive4u.StudentUI;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Student;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudentHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentHomeFragment extends StudentBaseFragment {

    private static final String TAG = StudentHomeFragment.class.getName();

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

    public StudentHomeFragment() {
        // Required empty public constructor
    }

    public static StudentHomeFragment newInstance(Student student) {
        StudentHomeFragment fragment = new StudentHomeFragment();
        Bundle args = newInstanceBaseArgs(student);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStudent = getArguments().getParcelable(ARG_STUDENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);

        if (mStudent == null && getArguments() == null) {
            Log.d(TAG, "Got a Null teacher and no arguments");
            return view;
        } else if (mStudent == null) {
            mStudent = getArguments().getParcelable(ARG_STUDENT);
        }

        BottomNavigationView navigation = (BottomNavigationView) view.findViewById(R.id.student_home_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        return view;
    }

}
