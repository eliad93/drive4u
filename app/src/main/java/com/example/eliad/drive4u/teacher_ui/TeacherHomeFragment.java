package com.example.eliad.drive4u.teacher_ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.ViewPagerAdapter;
import com.example.eliad.drive4u.chat.ChatUsersFragment;
import com.example.eliad.drive4u.chat.ChatsFragment;
import com.example.eliad.drive4u.models.Teacher;

public class TeacherHomeFragment extends TeacherBaseFragment  {

    private static final String TAG = TeacherHomeFragment.class.getName();

    private ViewPager mViewPager;
    private BottomNavigationView mNavigation;

    public void displayView(int viewId) {
        String msg = "BUG";
        switch (viewId) {
            case R.id.teacher_navigation_home:
                mViewPager.setCurrentItem(0);
                msg = "Home selected";
                break;
            case R.id.teacher_navigation_dashboard:
                mViewPager.setCurrentItem(1);
                msg = "Dashboard selected";
                break;
            case R.id.teacher_navigation_notifications:
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

        mNavigation = view.findViewById(R.id.teacher_home_navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mViewPager = view.findViewById(R.id.teacher_home_container);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(TeacherSummaryFragment.newInstance(mTeacher));
        viewPagerAdapter.addFragment(TeacherDashboardFragment.newInstance(mTeacher));
        viewPagerAdapter.addFragment(TeacherSummaryFragment.newInstance(mTeacher));
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
