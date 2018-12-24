package com.example.eliad.drive4u.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.LessonsAdapter;
import com.example.eliad.drive4u.adapters.StudentPluralInfoAdapter;
import com.example.eliad.drive4u.base_activities.StudentBaseActivity;
import com.example.eliad.drive4u.base_activities.TeacherBaseActivity;
import com.example.eliad.drive4u.models.Lesson;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class TeacherStudentInfoActivity extends TeacherBaseActivity {
    private static final String TAG = TeacherStudentInfoActivity.class.getName();

    private Student mStudent;
    private List<Lesson> lessonList;

    // widgets
    private TextView textViewStudentFirstName, textViewStudentLastName, textViewStudentEmail,
                     textViewStudentPhoneNumber, textViewStudentNumberOfLessons,
                     textViewStudentBalance;

    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_student_info);

        // teacher is initialized by the base class
        // we get the student here
        mStudent = parcelablesIntent.getParcelableExtra(StudentBaseActivity.ARG_STUDENT);

        // get all their common lessons
        initMutualLessons();

        // init the text views and set their content
        initTextViews();
        setTextViewsContent();

        initializeRecyclerView();

    }

    private void initializeRecyclerView() {
        Log.d(TAG, "in initializeRecyclerView");
        mRecyclerView = findViewById(R.id.TeacherStudentInfoLessonsRecycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void setTextViewsContent() {
        String text;
        textViewStudentFirstName.setText(mStudent.getFirstName());
        textViewStudentLastName.setText(mStudent.getLastName());
        textViewStudentPhoneNumber.setText(mStudent.getPhoneNumber());
        textViewStudentEmail.setText(mStudent.getEmail());
        text = Integer.toString(mStudent.getNumberOfLessons());
        textViewStudentNumberOfLessons.setText(text);
        text = Integer.toString(mStudent.getBalance());
        textViewStudentBalance.setText(text);
    }

    private void initTextViews() {
        textViewStudentFirstName        = findViewById(R.id.TeacherStudentInfoFistName);
        textViewStudentLastName         = findViewById(R.id.TeacherStudentInfoLastName);
        textViewStudentEmail            = findViewById(R.id.TeacherStudentInfoEmail);
        textViewStudentPhoneNumber      = findViewById(R.id.TeacherStudentInfoPhoneNumber);
        textViewStudentNumberOfLessons  = findViewById(R.id.TeacherStudentInfoNumberOfLessons);
        textViewStudentBalance          = findViewById(R.id.TeacherStudentInfoBalance);
    }

    private void initMutualLessons() {
        db.collection(getString(R.string.DB_Lessons))
                .whereEqualTo("studentUID", mStudent.getID())
                .whereEqualTo("teacherUID", mTeacher.getID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "initMutualLessons.onComplete");
                        if (task.isSuccessful()) {
                            lessonList = new LinkedList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Lesson lesson = document.toObject(Lesson.class);
                                Log.d(TAG, "current lesson teacher id: " + lesson.getTeacherUID() + " student id: " + lesson.getStudentUID());
                                lessonList.add(lesson);
                            }
                            Log.d(TAG, "Got " + lessonList.size() + " lessons");
                            if (lessonList.size() > 0) {
                                presentMutualLessons();
                            } else {
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void presentMutualLessons() {
        Log.d(TAG, "presentMutualLessons");
        mAdapter = new LessonsAdapter(lessonList);
        mRecyclerView.setAdapter(mAdapter);
    }

}
