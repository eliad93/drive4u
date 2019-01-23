package com.base.eliad.drive4u.teacher_ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.activities.LoginActivity;
import com.base.eliad.drive4u.base_activities.TeacherBaseActivity;
import com.base.eliad.drive4u.chat.MainChatActivity;
import com.base.eliad.drive4u.models.User;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherMainActivity extends TeacherBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // Tag for the Log
    private static final String TAG = TeacherMainActivity.class.getName();

    // key for passing the teacher between activities
    public static final String ARG_TEACHER = TAG + ".arg_teacher";
    public static final String ARG_NAV = TAG + ".arg_nav";

    Toolbar mToolbar;
    private boolean isAtHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_teacher_main);
        initDbVariables();
        initToolbar();
        initNavigationView();
        displayView(getIntent().getIntExtra(ARG_NAV, R.id.teacher_nav_home));
        status(User.ONLINE);
    }

    protected void initToolbar() {
        Log.d(TAG, "initToolbar");
        mToolbar = findViewById(R.id.toolbarTeacher);
        setSupportActionBar(mToolbar);
        String toolbarTitle = mTeacher.getFirstName() + " " + mTeacher.getLastName();
        mToolbar.setTitle(toolbarTitle);
    }

    protected void initNavigationView() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // a small and fun animation when opening and closing the teacher_home_navigation menu.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.teacher_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set the content of the menu.
        View headerView = navigationView.getHeaderView(0);
        CircleImageView drawerImage = headerView.findViewById(R.id.nav_header_image);
        TextView drawerUsername = headerView.findViewById(R.id.user_name);
        TextView drawerAccount = headerView.findViewById(R.id.user_email);

        if (mTeacher.getImageUrl() == null || mTeacher.getImageUrl().equals(User.DEFAULT_IMAGE_KEY)) {
            drawerImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(getApplicationContext()).load(mTeacher.getImageUrl()).into(drawerImage);
        }

        String userName = mTeacher.getFirstName() + " " + mTeacher.getLastName();
        drawerUsername.setText(userName);
        drawerAccount.setText(mTeacher.getEmail());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!isAtHome) {
            displayView(R.id.teacher_nav_home);
        } else {
            status(User.OFFLINE);
            super.onBackPressed();
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
            Toast.makeText(this, "Settings butten was pressed, not functional yet",
                    Toast.LENGTH_SHORT).show();
            // TODO: what do we want to do in the settings?!
            return true;
        } else if (id == R.id.action_logout) {
            logoutUser();
        }

        return super.onOptionsItemSelected(item);
    }

    public void logoutUser(){
        status(User.OFFLINE);
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(getBaseContext(), LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displayView(item.getItemId());
        return true;
    }

    public void displayView(int viewId) {
        Log.d(TAG, "displayView");
        Fragment fragment =  null;
        Intent intent = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.teacher_nav_home:
                fragment = TeacherHomeFragment.newInstance();
                isAtHome = true;
                break;

            case R.id.teacher_nav_schedule:
                intent = new Intent(this, TeacherSchedulerActivity.class);
                isAtHome = false;
                break;

            case R.id.teacher_nav_students:
                fragment =  TeacherStudentsFragment.newInstance();
                isAtHome = false;
                break;

            case R.id.teacher_nav_connection_requests:
                fragment =  TeacherConnectionRequestsFragment.newInstance();
                isAtHome = false;
                break;

            case R.id.teacher_nav_profile:
                fragment = TeacherProfileFragment.newInstance(mTeacher);
                isAtHome = false;
                break;

            case R.id.teacher_nav_send:
                intent = new Intent(this, MainChatActivity.class);
                intent.putExtra(MainChatActivity.ARG_USER, mTeacher);
                isAtHome = false;
                break;

            default:
                // TODO: add to the following isAtHome = false for back pressed.
                promptUserWithDialog(getString(R.string.no_content),
                        getString(R.string.not_ready_yet));
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        } else if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    private void status(String status) {
        mTeacher.setStatus(status);
        getTeacherDoc().update("status", status);
        writeTeacherToSharedPreferences();
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
}
