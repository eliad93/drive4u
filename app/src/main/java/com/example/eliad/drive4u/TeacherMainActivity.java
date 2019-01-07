package com.example.eliad.drive4u;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.eliad.drive4u.activities.LoginActivity;
import com.example.eliad.drive4u.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeacherMainActivity extends AppCompatActivity implements NavigationDrawerFragment.FragmentDrawerListener {

    private static final String TAG = TeacherMainActivity.class.getName();

    // key for passing the teacher between activities
    public static final String ARG_TEACHER = TAG + ".teacher_arg";

    // Intent for Parcelables
    protected Intent parcelablesIntent;
    // the user
    protected Teacher mTeacher;
    // Firebase
    protected FirebaseAuth mAuth;
    protected FirebaseUser mUser;
    protected FirebaseFirestore db;

    private Toolbar mToolbar;
    private NavigationDrawerFragment drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        Log.d(TAG, "onCreate");

        initDbVariables();
        parcelablesIntent = getIntent();
        mTeacher = parcelablesIntent.getParcelableExtra(ARG_TEACHER);

        if (mTeacher == null) {
            Log.d(TAG, "GOT A NULL TEACHER !!!!!!!!!!");

        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // set the toolbar title
        String name = getString(R.string.app_name) + "\t" + mTeacher.getFirstName() + " " + mTeacher.getLastName();
        getSupportActionBar().setTitle(name);
        displayView(0);
    }

    protected void initDbVariables() {
        Log.d(TAG, "get current firebase user");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }

        if (id == R.id.action_logout) {
            logoutUser();
        }

        if(id == R.id.action_search){
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = TeacherHomeFragment.newInstance(mTeacher);
                break;
            case 1:
                fragment = TeacherProfileFragment.newInstance(mTeacher);
                break;
            case 2:
                fragment = TeacherStudentsFragment.newInstance(mTeacher);
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

        }
    }

    /*
        all teacher activities should send an updated teacher to the prev activity
     */
    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra(ARG_TEACHER, mTeacher);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    /*
        all teacher activities should update their teacher
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                mTeacher = data.getParcelableExtra(ARG_TEACHER);
            }
        }
    }

    protected void myStartActivity(Class<? extends  AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(ARG_TEACHER, mTeacher);
        startActivity(intent);
    }

    protected void myStartActivityForResult(Class<? extends  AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(ARG_TEACHER, mTeacher);
        startActivityForResult(intent, 1);
    }

    public void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getBaseContext(), LoginActivity.class));
    }
}