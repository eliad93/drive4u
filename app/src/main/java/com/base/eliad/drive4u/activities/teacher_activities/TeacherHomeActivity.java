package com.base.eliad.drive4u.activities.teacher_activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import com.base.eliad.drive4u.R;

import com.base.eliad.drive4u.adapters.ViewPagerAdapter;
import com.base.eliad.drive4u.base_activities.TeacherBaseNavigationActivity;
import com.base.eliad.drive4u.fragments.UserNotificationsFragment;
import com.base.eliad.drive4u.teacher_ui.TeacherDashboardFragment;
import com.base.eliad.drive4u.teacher_ui.TeacherSummaryFragment;

public class TeacherHomeActivity extends TeacherBaseNavigationActivity {

    private static final String TAG = TeacherHomeActivity.class.getName();

    private ViewPager mViewPager;
    private BottomNavigationView mNavigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            displayBottomView(item.getItemId());
            return true;
        }
    };

    public void displayBottomView(int viewId) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        mNavigation = findViewById(R.id.teacher_home_navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mViewPager = findViewById(R.id.teacher_home_container);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
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
    }
}
