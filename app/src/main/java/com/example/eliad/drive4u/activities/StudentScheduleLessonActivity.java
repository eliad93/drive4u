package com.example.eliad.drive4u.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;


import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.StudentScheduleAdapter;
import com.example.eliad.drive4u.models.Lesson;
import com.example.eliad.drive4u.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class StudentScheduleLessonActivity extends AppCompatActivity {

    private TextView SelectedDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    // Intent for Parcelables
    private Intent parcelablesIntent;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Student mStudent;
    private Button submit;
    private TextView setStartingLocation;
    private TextView setEndingLocation;
    private String date;

    Lesson.Status[] hours = new Lesson.Status[14];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_schedule_lesson);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;

        submit = (Button) findViewById(R.id.add_lesson);
        setStartingLocation = (TextView) findViewById(R.id.setStartingLocation);
        setEndingLocation = (TextView) findViewById(R.id.setEndingLocation);

        parcelablesIntent = getIntent();

        mStudent = parcelablesIntent.getParcelableExtra("Student");

        db = FirebaseFirestore.getInstance();

        initializeRecyclerView();

        SelectedDate = (TextView) findViewById(R.id.selected_date);

        SelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        StudentScheduleLessonActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                date = day + "/" + month + "/" + year;
                SelectedDate.setText(date);
                updateDayView();
            }
        };

    }

    private void initializeRecyclerView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.hours_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }
    private void updateDayView(){
        db.collection(getString(R.string.DB_Lessons)).whereEqualTo("teacherUID", mStudent.getTeacherId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            LinkedList<Lesson> lessons = new LinkedList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Lesson lesson = document.toObject(Lesson.class);
                                lessons.addLast(lesson);
                            }
                            for(Lesson l : lessons){
                                int i = getArrayindexFromHour(l.getHour());
                                hours[i] = l.getConformationStatus();
                            }
                            mAdapter = new StudentScheduleAdapter(StudentScheduleLessonActivity.this, hours,date ,mStudent.getTeacherId(),mStudent.getID());
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    }
                });
    }
    private Integer getArrayindexFromHour(String hour){
        switch (hour){
            case "07:00":
                return 0;
            case "08:00":
                return 1;
            case "09:00":
                return 2;
            case "10:00":
                return 3;
            case "11:00":
                return 4;
            case "12:00":
                return 5;
            case "13:00":
                return 6;
            case "14:00":
                return 7;
            case "15:00":
                return 8;
            case "16:00":
                return 9;
            case "17:00":
                return 10;
            case "18:00":
                return 11;
            case "19:00":
                return 12;
            case "20:00":
                return 13;
            default:
                return -1;

        }
    }


}
