package com.example.eliad.drive4u.student_ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.activities.LoginActivity;
import com.example.eliad.drive4u.chat.MainChatActivity;
import com.example.eliad.drive4u.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.example.eliad.drive4u.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = StudentMainActivity.class.getName();

    public static final String ARG_STUDENT = TAG + ".arg_student";
    public static final String ARG_NAV = TAG + ".arg_nav";

    // the user
    protected Student mStudent;
    // Firebase
    protected FirebaseAuth mAuth;
    protected FirebaseUser mUser;
    protected FirebaseFirestore db;

    Toolbar mToolbar;
    private boolean isAtHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        Log.d(TAG, "onCreate");

        initDbVariables();
        initStudent();
        initToolbar();
        initNavigationView();
        displayView(getIntent().getIntExtra(ARG_NAV, R.id.student_nav_home));
        status(User.ONLINE);
    }

    public void displayView(int viewId) {
        Log.d(TAG, "displayView");
        Fragment fragment = null;
        Intent intent = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.student_nav_home:
                fragment = StudentHomeFragment.newInstance(mStudent);
                isAtHome = true;
                break;
            case R.id.student_nav_teachers:
                fragment = StudentSearchTeacherFragment.newInstance(mStudent);
                isAtHome = false;
                break;
            case R.id.student_nav_lessons_archive:
                // TODO: change small icon
                fragment = StudentLessonsArchiveFragment.newInstance(mStudent);
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
                Toast.makeText(this, "this teacher_home_navigation item was not implemented yet", Toast.LENGTH_LONG).show();
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
        } else if (!isAtHome) {
            displayView(R.id.student_nav_home);
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
            return true;
        } else if (id == R.id.action_logout) {
            logoutUser();
        }

        return super.onOptionsItemSelected(item);
    }

    public void logoutUser() {
        status(User.OFFLINE);
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(getBaseContext(), LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

    private void initToolbar() {
        Log.d(TAG, "initToolbar");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        String toolbarTitle = mStudent.getFirstName() + " " + mStudent.getLastName();
        mToolbar.setTitle(toolbarTitle);
    }

    private void initStudent() {
        Log.d(TAG, "initTeacher");
        Intent parcelablesIntent = getIntent();
        mStudent = parcelablesIntent.getParcelableExtra(ARG_STUDENT);

        if (mStudent == null) {
            Log.d(TAG, " Got an intent without a student. fetching him fom the dp.");
            db.collection(getString(R.string.DB_Students))
                    .document(mUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot != null && snapshot.exists()) {
                                    mStudent = snapshot.toObject(Student.class);
                                } else {
                                    Log.d(TAG, "Task returned empty!");
                                }
                            } else {
                                Log.d(TAG, "Task failed to get a student");
                            }
                        }
                    });
        }
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
        CircleImageView drawerImage = (CircleImageView) headerView.findViewById(R.id.nav_header_image);
        TextView drawerUsername = (TextView) headerView.findViewById(R.id.user_name);
        TextView drawerAccount = (TextView) headerView.findViewById(R.id.user_email);
        String userName = mStudent.getFirstName() + " " + mStudent.getLastName();
        drawerUsername.setText(userName);
        drawerAccount.setText(mStudent.getEmail());

        if (mStudent.getImageUrl() == null || mStudent.getImageUrl().equals(User.DEFAULT_IMAGE_KEY)) {
            drawerImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(getApplicationContext()).load(mStudent.getImageUrl()).into(drawerImage);
        }
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
        updateData();
        status(User.ONLINE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status(User.OFFLINE);
    }

    private void updateData(){
        Log.d(TAG, "updateData");
        disableUserInteraction();
        db.collection("Students").document(mUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            if(document.exists()){
                                mStudent = document.toObject(Student.class);
                                Log.d(TAG, "updated student");
                                if(!mStudent.hasTeacher()){

                                }
                            }
                        } else {
                            Log.d(TAG, "failed to update student");
                        }
                        enableUserInteraction();
                    }
                });
    }

    private void disableUserInteraction() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void enableUserInteraction() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
