package com.example.eliad.drive4u.student_ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.ViewPagerAdapter;
import com.example.eliad.drive4u.models.Student;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudentHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentHomeFragment extends StudentBaseFragment {

    private static final String TAG = StudentHomeFragment.class.getName();

    private ViewPager mViewPager;
    private BottomNavigationView mNavigation;

    public void displayView(int viewId) {
        String msg = "BUG";
        switch (viewId) {
            case R.id.student_navigation_home:
                mViewPager.setCurrentItem(0);
                msg = "Home selected";
                break;

            case R.id.student_navigation_dashboard:
                mViewPager.setCurrentItem(1);
                msg = "Dashboard selected";
                break;

            case R.id.student_navigation_notifications:
                mViewPager.setCurrentItem(2);
                msg = "notifications selected";
                break;
        }

        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            displayView(item.getItemId());
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

        mNavigation = view.findViewById(R.id.student_home_navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mViewPager = view.findViewById(R.id.student_home_container);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(StudentSummaryFragment.newInstance(mStudent));
        viewPagerAdapter.addFragment(StudentDashboardFragment.newInstance(mStudent));
        viewPagerAdapter.addFragment(StudentSummaryFragment.newInstance(mStudent));
        mViewPager.setAdapter(viewPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        mNavigation.setSelectedItemId(R.id.teacher_navigation_home);
                        break;
                    case 1:
                        mNavigation.setSelectedItemId(R.id.teacher_navigation_dashboard);
                        break;
                    case 2:
                        mNavigation.setSelectedItemId(R.id.teacher_navigation_notifications);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        return view;
    }

}
