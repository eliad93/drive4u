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
import com.example.eliad.drive4u.base_activities.StudentBaseActivity;
import com.example.eliad.drive4u.built_in_utils.BorderLineDividerItemDecoration;
import com.example.eliad.drive4u.models.Lesson;
import com.example.eliad.drive4u.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class StudentScheduleLessonActivity extends StudentBaseActivity
        implements StudentScheduleAdapter.OnItemClickListener{

    private String[] hours = new String[]{"07:00", "08:00","09:00","10:00","11:00","12:00","13:00","14:00",
            "15:00","16:00","17:00","18:00","19:00","20:00", "21:00"};


    private TextView selecteDate;
    private TextView dateSelected;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Calendar cal;
    // Intent for Parcelables
    private Intent parcelablesIntent;

    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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


        submit = (Button) findViewById(R.id.add_lesson);

        cal = Calendar.getInstance();
        initializeRecyclerView();
        dateSelected = (TextView) findViewById(R.id.date_selected);
        selecteDate = (TextView) findViewById(R.id.selecte_date);

        setCurrentDay();

        selecteDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                dateSelected.setText(date);
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
        mRecyclerView.addItemDecoration(new BorderLineDividerItemDecoration(this));
    }



    private void updateDayView(){
        hoursStatus = new Lesson.Status[14];
        lessons = new LinkedList<>();
        db.collection("lessons").whereEqualTo("teacherUID", mStudent.getTeacherId()).whereEqualTo("date",date)
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
                if(l.getConformationStatus() == Lesson.Status.S_REQUEST){
                    Toast.makeText(this,R.string.already_schedule_this_hour,Toast.LENGTH_SHORT).show();
                    return;
                }
                if(l.getConformationStatus() == Lesson.Status.T_UPDATE){
                    Toast.makeText(this,R.string.teacher_update_lesson_to_hour,Toast.LENGTH_SHORT).show();
                    return;
                }
                if(l.getConformationStatus() == Lesson.Status.S_UPDATE){
                    Toast.makeText(this,R.string.wait_for_teacher_response,Toast.LENGTH_SHORT).show();
                    return;
                }
                if(l.getConformationStatus() == Lesson.Status.S_CONFIRMED){
                    Toast.makeText(this,R.string.wait_for_teacher_response,Toast.LENGTH_SHORT).show();
                    return;
                }
                if(l.getConformationStatus() == Lesson.Status.T_CONFIRMED){
                    Toast.makeText(this,R.string.hour_is_taken,Toast.LENGTH_SHORT).show();
                    return;
                }
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
        Lesson lesson = new Lesson(mStudent.getTeacherId(), mStudent.getID(), date,lessonStartingTime.getText().toString(),
                "1","1", Lesson.Status.S_REQUEST);

       db.collection("lessons").add(lesson).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    String id = task.getResult().getId();
                    db.collection("lessons").document(id).update("lessonID",id);
                    Toast.makeText(StudentScheduleLessonActivity.this, R.string.request_sent, Toast.LENGTH_SHORT).show();
                    lessonCreate.hide();
                }
            }
        });

    }

    private void setCurrentDay(){
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        date = currentDay + "/" + currentMonth + "/" + currentYear;
        dateSelected.setText(date);
        updateDayView();

    }
}
