package com.example.eliad.drive4u.TeacherUI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.activities.LoginActivity;
import com.example.eliad.drive4u.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeacherMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Tag for the Log
    private static final String TAG = TeacherMainActivity.class.getName();

    // key for passing the teacher between activities
    public static final String ARG_TEACHER = TAG + ".arg_teacher";

    // the user
    protected Teacher mTeacher;
    // Firebase
    protected FirebaseAuth mAuth;
    protected FirebaseUser mUser;
    protected FirebaseFirestore db;

    Toolbar mToolbar;
    private boolean isAtHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        Log.d(TAG, "onCreate");

        initDbVariables();

        initTeacher();

        initToolbar();
        initNavigationView();
        displayView(R.id.nav_home);
    }

    protected void initToolbar() {
        Log.d(TAG, "initToolbar");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        String toolbarTitle = mTeacher.getFirstName() + " " + mTeacher.getLastName();
        mToolbar.setTitle(toolbarTitle);
    }

    protected void initTeacher() {
        Log.d(TAG, "initTeacher");
        Intent parcelablesIntent = getIntent();
        mTeacher = parcelablesIntent.getParcelableExtra(ARG_TEACHER);

        if (mTeacher == null) {
            Log.d(TAG, " Got an intent with out a teacher. fetching him fom the dp. fix this!");
            // TODO: fetch the teacher or search for the bug
        }
    }

    protected void initNavigationView() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // a small and fun animation when opening and closing the teacher_home_navigation menu.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set the content of the menu.
        View headerView = navigationView.getHeaderView(0);
        ImageView drawerImage = (ImageView) headerView.findViewById(R.id.imageView);
        TextView drawerUsername = (TextView) headerView.findViewById(R.id.user_name);
        TextView drawerAccount = (TextView) headerView.findViewById(R.id.user_email);
        // TODO: Eliad: drawerImage.setImageDrawable(R.drawable.ic_user);
        String userName = mTeacher.getFirstName() + " " + mTeacher.getLastName();
        drawerUsername.setText(userName);
        drawerAccount.setText(mTeacher.getEmail());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!isAtHome) {
            displayView(R.id.nav_home);
        } else {
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
            Toast.makeText(this, "Settings butten was pressed, not functional yet", Toast.LENGTH_SHORT).show();
            // TODO: what do we want to do in the settings?!
            return true;
        } else if (id == R.id.action_logout) {
            logoutUser();
        }

        return super.onOptionsItemSelected(item);
    }

    public void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getBaseContext(), LoginActivity.class));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displayView(item.getItemId());
        return true;
    }

    protected void initDbVariables() {
        Log.d(TAG, "get current firebase user");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;
        db = FirebaseFirestore.getInstance();
    }

    public void displayView(int viewId) {
        Log.d(TAG, "displayView");
        Fragment fragment =  null;
        Intent intent = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_home:
                fragment = TeacherHomeFragment.newInstance(mTeacher);
                isAtHome = true;
                break;
            case R.id.nav_profile:
                fragment = TeacherProfileFragment.newInstance(mTeacher);
                isAtHome = false;
                break;

            case R.id.nav_students:
                fragment =  TeacherStudentsFragment.newInstance(mTeacher);
                isAtHome = false;
                break;

            case R.id.nav_send:
                fragment = TeacherMainChatFragment.newInstance(mTeacher);
                isAtHome = false;
                break;

            default:
                // TODO: add to the following isAtHome = false for back pressed.
                Toast.makeText(this, "this teacher_home_navigation item was not implemented yet", Toast.LENGTH_LONG).show();
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        } else if (intent != null) {
            finish();
            startActivity(intent);
        }
        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }
}
