package com.example.eliad.drive4u.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import java.util.Calendar;
import java.util.LinkedList;


import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.StudentScheduleAdapter;
import com.example.eliad.drive4u.adapters.TeacherSearchAdapter;
import com.example.eliad.drive4u.models.Lesson;
import com.example.eliad.drive4u.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class StudentScheduleLessonActivity extends AppCompatActivity
        implements StudentScheduleAdapter.OnItemClickListener{

    private String[] hours = new String[]{"07:00", "08:00","09:00","10:00","11:00","12:00","13:00","14:00",
            "15:00","16:00","17:00","18:00","19:00","20:00", "21:00"};


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
    LinkedList<Lesson> lessons;


    //create new lesson
    private Button submit;
    private TextView lessonStartingTime;
    private TextView lessonEndingTime;
    private TextView lessonStartingLocation;
    private TextView lessonEndingLocation;
    private TextView lessonDate;
    private String date;
    private Dialog lessonCreate;

    Lesson.Status[] hoursStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_schedule_lesson);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;

        submit = (Button) findViewById(R.id.add_lesson);

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
        mRecyclerView.clearOnChildAttachStateChangeListeners();
    }



    private void updateDayView(){
        hoursStatus = new Lesson.Status[14];
        lessons = new LinkedList<>();
        db.collection(getString(R.string.DB_Lessons)).whereEqualTo("teacherUID", mStudent.getTeacherId()).whereEqualTo("date",date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Lesson lesson = document.toObject(Lesson.class);
                                lessons.addLast(lesson);
                            }
                            for(Lesson l : lessons){
                                int i = getArrayindexFromHour(l.getHour());
                                if(hoursStatus[i] != Lesson.Status.S_CONFIRMED && hoursStatus[i] != Lesson.Status.S_CONFIRMED){
                                    hoursStatus[i] = l.getConformationStatus();
                                }
                            }
                            mAdapter = new StudentScheduleAdapter(StudentScheduleLessonActivity.this, hoursStatus);
                            ((StudentScheduleAdapter) mAdapter)
                                    .setOnItemClickListener(StudentScheduleLessonActivity.this);
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


    @Override
    public void onItemClick(final int position) {
        for(Lesson l: lessons){
            if(l.getHour().equals(hours[position]) && l.getStudentUID().equals(mStudent.getID())){
                Toast.makeText(this,R.string.already_schedule_this_hour,Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mRecyclerView.findViewHolderForAdapterPosition(position).itemView.setBackgroundColor(Color.GREEN);
        lessonCreate = new Dialog(this);
        lessonCreate.setContentView(R.layout.student_schedule_lesson_dialog);

        lessonStartingTime = (TextView) lessonCreate.findViewById(R.id.starting_time);
        lessonEndingTime = (TextView) lessonCreate.findViewById(R.id.ending_time);
        lessonStartingLocation = (TextView) lessonCreate.findViewById(R.id.starting_location);
        lessonEndingLocation = (TextView) lessonCreate.findViewById(R.id.ending_location);
        lessonDate = (TextView) lessonCreate.findViewById(R.id.lesson_date);
        submit = (Button) lessonCreate.findViewById(R.id.add_lesson);

        lessonStartingTime.setText(hours[position]);
        lessonEndingTime.setText(hours[position+1]);
        lessonDate.setText(date);

        lessonCreate.show();
        lessonCreate.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(hoursStatus[position] == Lesson.Status.S_CONFIRMED ||hoursStatus[position] == Lesson.Status.S_UPDATE || hoursStatus[position] == Lesson.Status.S_UPDATE || hoursStatus[position] == Lesson.Status.S_REQUEST) {
                    mRecyclerView.findViewHolderForAdapterPosition(position).itemView.setBackgroundColor(Color.YELLOW);
                }else{
                    mRecyclerView.findViewHolderForAdapterPosition(position).itemView.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addLesson();
            }
        });
    }

    public void addLesson(){
        Lesson lesson = new Lesson( mStudent.getTeacherId(), mStudent.getID(), date,lessonStartingTime.getText().toString(),
                "1","1", Lesson.Status.S_REQUEST);

        db.collection("lessons").add(lesson);
        Toast.makeText(this, R.string.request_sent, Toast.LENGTH_SHORT).show();
        lessonCreate.hide();

    }
}
