package com.example.eliad.drive4u.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.activities.LoginActivity;
import com.example.eliad.drive4u.models.Chat;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainChatActivity extends AppCompatActivity {

    private static final String TAG = MainChatActivity.class.getName();

    public static final String ARG_USER = TAG + ".arg_user";

    private ViewPager mViewPager;
    private BottomNavigationView mNavigation;
    private Toolbar mToolbar;
    private User mUser;
    private DatabaseReference reference;

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
        mNavigation = findViewById(R.id.bottom_main_chat_navigation);
        mViewPager = findViewById(R.id.main_chat_frame);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(ChatsFragment.newInstance(mUser));
        viewPagerAdapter.addFragment(ChatUsersFragment.newInstance(mUser));

        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        mNavigation.setSelectedItemId(R.id.navigation_chats);
                        break;
                    case 1:
                        mNavigation.setSelectedItemId(R.id.navigation_users);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {}
        });
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        displayView(R.id.navigation_chats);

        reference = FirebaseDatabase.getInstance().getReference(ChatMessageActivity.CHATS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(mUser.getID()) && !chat.isSeen()) {
                        unread++;
                    }
                }
                String title = "Chats";
                if (unread != 0) {
                    title = "(" + unread + ")" + title;
                }
                mNavigation.getMenu().getItem(0).setTitle(title);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        status(User.OFFLINE);
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(getBaseContext(), LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void status(final String status) {
        mUser.setStatus(status);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String collection_path = mUser instanceof Teacher ? "Teachers" : "Students";
        db.collection(collection_path)
                .document(mUser.getID())
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
}
