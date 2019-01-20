package com.example.eliad.drive4u.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.student_ui.StudentMainActivity;
import com.example.eliad.drive4u.teacher_ui.TeacherMainActivity;
import com.example.eliad.drive4u.adapters.ViewPagerAdapter;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.example.eliad.drive4u.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainChatActivity extends AppCompatActivity {

    private static final String TAG = MainChatActivity.class.getName();

    public static final String ARG_USER = TAG + ".arg_user";

    private ViewPager mViewPager;
    private BottomNavigationView mNavigation;
    private Toolbar mToolbar;
    private User mUser;

    public void displayView(int viewId) {
        Log.d(TAG, "displayView");

        String msg = "BUG";
        switch (viewId) {
            case R.id.navigation_chats:
                msg = "Chats selected";
                mViewPager.setCurrentItem(0);

                break;
            case R.id.navigation_users:
                msg = "users selected";
                mViewPager.setCurrentItem(1);
                break;
        }
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        mToolbar = findViewById(R.id.chat_message_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (mUser instanceof Student) {
                    intent = new Intent(MainChatActivity.this,
                            StudentMainActivity.class);
                    intent.putExtra(StudentMainActivity.ARG_STUDENT, mUser);
                } else {
                    // instanceof Teacher
                    intent = new Intent(MainChatActivity.this,
                            TeacherMainActivity.class);
                    intent.putExtra(TeacherMainActivity.ARG_TEACHER, mUser);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        getUser();
        mNavigation = (BottomNavigationView) findViewById(R.id.bottom_main_chat_navigation);
        mViewPager = findViewById(R.id.main_chat_frame);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(ChatsFragment.newInstance(mUser));
        viewPagerAdapter.addFragment(ChatUsersFragment.newInstance(mUser));

        mViewPager.setAdapter(viewPagerAdapter);

        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        displayView(R.id.navigation_chats);
    }

    private void getUser() {
        mUser = getIntent().getParcelableExtra(ARG_USER);

        if (mUser != null)
            return;

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(getString(R.string.DB_Students))
                .document(fuser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();

                            if (snapshot != null && snapshot.exists()) {
                                mUser = snapshot.toObject(Student.class);
                            } else {
                                db.collection(getString(R.string.DB_Students))
                                        .document(fuser.getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot snapshot = task.getResult();

                                                    if (snapshot != null && snapshot.exists()) {
                                                        mUser = snapshot.toObject(Teacher.class);
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void status(final String status) {
        mUser.setStatus(status);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(getString(R.string.DB_Teachers))
                .document(mUser.getID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                db.collection(getString(R.string.DB_Teachers))
                                        .document(mUser.getID())
                                        .update("status", status);
                            } else {
                                db.collection(getString(R.string.DB_Students))
                                        .document(mUser.getID())
                                        .update("status", status);
                            }
                        }
                    }
                });
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
