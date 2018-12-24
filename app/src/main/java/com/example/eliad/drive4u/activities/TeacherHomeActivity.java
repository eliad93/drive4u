package com.example.eliad.drive4u.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.LessonsAdapter;
import com.example.eliad.drive4u.base_activities.TeacherBaseActivity;
import com.example.eliad.drive4u.models.Lesson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

public class TeacherHomeActivity extends TeacherBaseActivity {

    // Log
    private static final String TAG = TeacherHomeActivity.class.getName();

    // widgets
    private TextView textViewLessonsRemainingToday;
    private TextView textViewNoLessons;

    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        Log.d(TAG, "onCreate");

        textViewLessonsRemainingToday = findViewById(R.id.textViewLessonsRemainingToday);
        textViewNoLessons = findViewById(R.id.TeacherHomeTextViewNoLessosns);

        textViewLessonsRemainingToday.setText("0"); // TODO

        initLessonsRecyclerView();

        presentNextLessons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.schedule){
            Intent intent = new Intent(this, TeacherScheduleActivity.class);
            intent.putExtra(TeacherBaseActivity.ARG_TEACHER, mTeacher);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.students_requests){
            //students requests activity
        }
        if(item.getItemId() == R.id.profile){
            //profile activity
            myStartActivity(TeacherProfileActivity.class);
        }
        if(item.getItemId() == R.id.my_students){
            //all students info activity
            myStartActivity(TeacherMyStudentsActivity.class);
        }
        if(item.getItemId() == R.id.budget_management){
            //budget management activity
        }
        if(item.getItemId() == R.id.logout){
            logoutUser();
        }
        return super.onOptionsItemSelected(item);
    }
    public void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getBaseContext(), LoginActivity.class));
    }

    private void presentNextLessons() {
        Log.d(TAG, "in presentNextLessons");

        db.collection(getString(R.string.DB_Lessons))
                .whereEqualTo(getString(R.string.DB_Teacher), mTeacher.getID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            LinkedList<Lesson> lessons = new LinkedList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "received document " + document.getId());
                                Lesson lesson = document.toObject(Lesson.class);
                                lessons.addLast(lesson);
                            }
                            if (lessons.size() > 0 ) {
                                mAdapter = new LessonsAdapter(lessons);
                                mRecyclerView.setAdapter(mAdapter);
                            } else {
                                Log.d(TAG, "Teacher " + mTeacher.getEmail() + " has no lessons to present");
                                textViewNoLessons.setText(R.string.you_have_no_lessons);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void initLessonsRecyclerView() {
        Log.d(TAG, "in initializeNextLessonsRecyclerView");
        mRecyclerView = findViewById(R.id.recyclerViewTeacherLessons);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }
}
