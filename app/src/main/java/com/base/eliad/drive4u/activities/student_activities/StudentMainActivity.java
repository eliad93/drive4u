package com.base.eliad.drive4u.activities.student_activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.activities.LoginActivity;
import com.base.eliad.drive4u.adapters.ViewPagerAdapter;
import com.base.eliad.drive4u.base_activities.StudentBaseActivity;
import com.base.eliad.drive4u.chat.MainChatActivity;
import com.base.eliad.drive4u.fragments.UserNotificationsFragment;
import com.base.eliad.drive4u.models.User;
import com.base.eliad.drive4u.student_ui.StudentDashboardFragment;
import com.base.eliad.drive4u.student_ui.StudentLessonsArchiveFragment;
import com.base.eliad.drive4u.student_ui.StudentProfileFragment;
import com.base.eliad.drive4u.student_ui.StudentSummaryFragment;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentMainActivity extends StudentBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = StudentMainActivity.class.getName();
    public static final String ARG_NAV = TAG + ".arg_nav";
    // widgets
    Toolbar mToolbar;
    private ViewPager mViewPager;
    private BottomNavigationView mNavigation;

    private boolean isAtHome;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            displayPage(item.getItemId());
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        Log.d(TAG, "onCreate");
        initWidgets();
        status(User.ONLINE);
    }

    private void initWidgets() {
        initToolbar();
        initNavigationView();

        mNavigation = findViewById(R.id.student_home_navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mViewPager = findViewById(R.id.student_home_container);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(StudentSummaryFragment.newInstance());
        viewPagerAdapter.addFragment(StudentDashboardFragment.newInstance());
        viewPagerAdapter.addFragment(UserNotificationsFragment.newInstance(mStudent));
        mViewPager.setAdapter(viewPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        mNavigation.setSelectedItemId(R.id.student_navigation_home);
                        break;
                    case 1:
                        mNavigation.setSelectedItemId(R.id.student_navigation_dashboard);
                        break;
                    case 2:
                        mNavigation.setSelectedItemId(R.id.student_navigation_notifications);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public void displayPage(int viewId) {
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
        Log.d(TAG, msg);
    }

    public void displayView(int viewId) {
        Log.d(TAG, "displayView");
        saveData();
        Fragment fragment = null;
        Intent intent = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.student_nav_home:
                if(isAtHome){
                    break;
                }
                intent = new Intent(this, StudentMainActivity.class);
                isAtHome = true;
                break;
            case R.id.student_nav_schedule:
                if(mStudent.hasPendingRequest()){
                    promptUserWithDialog(
                            getString(R.string.student_pending_connection_request_title),
                            getString(R.string.student_pending_connection_request_message));
                    break;
                }
                if(!mStudent.hasTeacher()){
                    promptUserWithDialog(getString(R.string.no_teacher_dialog_title),
                            getString(R.string.no_teacher_dialog_message));
                    break;
                }
                intent = new Intent(this, StudentSchedulerLessonActivity.class);
                isAtHome = false;
                break;
            case R.id.student_nav_lessons_archive:
                // TODO: change small icon
                fragment = StudentLessonsArchiveFragment.newInstance(mStudent);
                isAtHome = false;
                break;
            case R.id.student_nav_teachers:
                intent = new Intent(this, StudentSearchTeacherActivity.class);
                isAtHome = false;
                break;
            case R.id.student_nav_profile:
                fragment = StudentProfileFragment.newInstance(mStudent);
                isAtHome = false;
                break;

            case R.id.student_nav_send:
                intent = new Intent(this, MainChatActivity.class);
                intent.putExtra(MainChatActivity.ARG_USER, mStudent);
                isAtHome = false;
                break;

            default:
                // TODO: add to the following isAtHome = false for back pressed.
                Toast.makeText(this,
                        "this teacher_home_navigation item was not implemented yet",
                        Toast.LENGTH_LONG).show();
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.student_content_frame, fragment);
            ft.commit();
        } else if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = findViewById(R.id.student_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "in onBackPressed");
        updateData();
        DrawerLayout drawer = findViewById(R.id.student_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            logoutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    public void logoutUser() {
        status(User.OFFLINE);
        saveData();
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displayView(item.getItemId());
        return true;
    }

    private void initToolbar() {
        Log.d(TAG, "initToolbar");
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        String toolbarTitle = mStudent.getFirstName() + " " + mStudent.getLastName();
        mToolbar.setTitle(toolbarTitle);
    }

    private void initNavigationView() {
        DrawerLayout drawer = findViewById(R.id.student_drawer_layout);
        // a small and fun animation when opening and closing the teacher_home_navigation menu.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // set the content of the menu.
        View headerView = navigationView.getHeaderView(0);
        CircleImageView drawerImage = headerView.findViewById(R.id.nav_header_image);
        TextView drawerUsername = headerView.findViewById(R.id.user_name);
        TextView drawerAccount = headerView.findViewById(R.id.user_email);
        String userName = mStudent.getFirstName() + " " + mStudent.getLastName();
        drawerUsername.setText(userName);
        drawerAccount.setText(mStudent.getEmail());
        setStudentImage(drawerImage);
    }

    protected void status(final String status) {
        mStudent.setStatus(status);
        db.collection(getString(R.string.DB_Students))
                .document(mStudent.getID())
                .update("status", status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status(User.ONLINE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status(User.OFFLINE);
    }

    @Override
    protected void saveData() {
        super.saveData();
    }

    @Override
    protected void updateData(){
        super.updateData();
        Log.d(TAG, "updateData");
    }
}