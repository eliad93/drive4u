package com.base.eliad.drive4u.teacher_ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.adapters.ViewPagerAdapter;
import com.base.eliad.drive4u.fragments.UserNotificationsFragment;

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
//        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        Log.d(TAG, msg);
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


    public static TeacherHomeFragment newInstance() {
        return new TeacherHomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_teacher_home, container, false);

        mNavigation = view.findViewById(R.id.teacher_home_navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mViewPager = view.findViewById(R.id.teacher_home_container);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(TeacherSummaryFragment.newInstance());
        viewPagerAdapter.addFragment(TeacherDashboardFragment.newInstance());
        viewPagerAdapter.addFragment(UserNotificationsFragment.newInstance(mTeacher));
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
