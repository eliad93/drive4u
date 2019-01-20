package com.example.eliad.drive4u.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.activities.LoginActivity;
import com.example.eliad.drive4u.adapters.ViewPagerAdapter;

public class RegistrationMainActivity extends AppCompatActivity {

    private static final String TAG = RegistrationMainActivity.class.getName();

    private ViewPager mViewPager;
    private BottomNavigationView mNavigation;
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Log.d(TAG, "onNavigationItemSelected");
            displayView(item.getItemId());
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_main);

        Log.d(TAG, "onCreate");

        mNavigation = (BottomNavigationView) findViewById(R.id.registration_bottom_navigation);
        mViewPager = findViewById(R.id.main_registration_frame);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new StudentRegistrationFragment());
        viewPagerAdapter.addFragment(new TeacherRegistrationFragment());

        mViewPager.setAdapter(viewPagerAdapter);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        mNavigation.setSelectedItemId(R.id.navigation_register_student);
                        break;
                    case 1:
                        mNavigation.setSelectedItemId(R.id.navigation_register_teacher);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        displayView(R.id.navigation_register_student);
    }

    private void displayView(int viewId) {
        Log.d(TAG, "displayView");
        String msg = "BUG";
        switch (viewId) {
            case R.id.navigation_register_student:
                msg = "register student selected";
                mViewPager.setCurrentItem(0);
                break;
            case R.id.navigation_register_teacher:
                msg = "register teacher selected";
                mViewPager.setCurrentItem(1);
                break;
        }
        Log.d(TAG, msg);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(intent);
    }
}
