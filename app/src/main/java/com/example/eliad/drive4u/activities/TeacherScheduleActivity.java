package com.example.eliad.drive4u.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.StudentScheduleAdapter;
import com.example.eliad.drive4u.adapters.TeacherChooseStudentLessonAdapter;
import com.example.eliad.drive4u.adapters.TeacherScheduleAdapter;
import com.example.eliad.drive4u.base_activities.TeacherBaseActivity;
import com.example.eliad.drive4u.built_in_utils.BorderLineDividerItemDecoration;
import com.example.eliad.drive4u.fragments.TimePickerFragment;
import com.example.eliad.drive4u.models.Lesson;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TeacherScheduleActivity extends TeacherBaseActivity implements TeacherScheduleAdapter.OnItemClickListener, TeacherChooseStudentLessonAdapter.OnItemClickListener, TimePickerDialog.OnTimeSetListener {

    private String[] hours = new String[]{"07:00", "08:00","09:00","10:00","11:00","12:00","13:00","14:00",
            "15:00","16:00","17:00","18:00","19:00","20:00", "21:00"};

    private TextView selecteDate;
    private TextView dateSelected;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Calendar cal;

    //recyclerView for schedule recyclerView
    private RecyclerView scheduleRecyclerView;
    private RecyclerView.Adapter scheduleAdapter;
    private RecyclerView.LayoutManager scheduleLayoutManager;


    private String date;

    //lessons hours lists
    private Lesson.Status[] hoursStatus;
    private LinkedList<Lesson> list0;
    private LinkedList<Lesson> list1;
    private LinkedList<Lesson> list2;
    private LinkedList<Lesson> list3;
    private LinkedList<Lesson> list4;
    private LinkedList<Lesson> list5;
    private LinkedList<Lesson> list6;
    private LinkedList<Lesson> list7;
    private LinkedList<Lesson> list8;
    private LinkedList<Lesson> list9;
    private LinkedList<Lesson> list10;
    private LinkedList<Lesson> list11;
    private LinkedList<Lesson> list12;
    private LinkedList<Lesson> list13;
    private ArrayList<LinkedList<Lesson>> allLessons;

    //students requests for hour dialog
    private Dialog studentsRequests;
    private RecyclerView requestsRecyclerView; //recyclerView for students requests
    private RecyclerView.Adapter requestsAdapter;
    private RecyclerView.LayoutManager requestsLayoutManager;
    private TextView dateDialog;
    private TextView hourDialog;
    private Button finishButton;
    private boolean oneRequestApprove = false;

    //updated lesson Status
    private Lesson.Status updateStatus;
    private Lesson updateLesson;

    //teacher update student request for lesson dialog
    private Dialog updateRequests;
    private TextView dateUpdateDialog;
    private TextView hourUpdateDialog;
    private TextView studentNameUpdateDialog;
    private TextView startingLocationUpdateDialog;
    private TextView endingLocationUpdateDialog;
    private Button update;
    private Button timePicker;
    private Button datePicker;
    private DatePickerDialog.OnDateSetListener updateRequestsDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_schedule);
        cal = Calendar.getInstance();

        initializeScheduleRecyclerView();
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
                        TeacherScheduleActivity.this,
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



    private void updateDayView(){
        oneRequestApprove = false;
        hoursStatus = new Lesson.Status[14];
        updateLessons();
    }

    @Override
    public void onItemClickTeacherSchedule(final int position) {
        studentsRequests = new Dialog(this);
        studentsRequests.setContentView(R.layout.teacher_choose_student_schedule_lesson_dialog);
        dateDialog = (TextView) studentsRequests.findViewById(R.id.date_dialog);
        hourDialog = (TextView) studentsRequests.findViewById(R.id.hour_dialog);
        finishButton = (Button) studentsRequests.findViewById(R.id.finish_button);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closingStudentsRequestsDialog(position);
            }
        });
        String dateMessage = dateDialog.getText().toString() + " " + date;
        String hourMessage = hourDialog.getText().toString() + " " +hours[position];
        dateDialog.setText(dateMessage);
        hourDialog.setText(hourMessage);
        initializerequestsRecyclerView();
        studentsRequests.show();
        studentsRequests.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                closingStudentsRequestsDialog(position);
            }
        });
        requestsAdapter = new TeacherChooseStudentLessonAdapter(TeacherScheduleActivity.this, allLessons.get(position),position);
        ((TeacherChooseStudentLessonAdapter) requestsAdapter)
                .setOnItemClickListener(TeacherScheduleActivity.this);
        requestsRecyclerView.setAdapter(requestsAdapter);
    }

    private void initializeScheduleRecyclerView(){
        scheduleRecyclerView = (RecyclerView) findViewById(R.id.hours_list);
        scheduleRecyclerView.setHasFixedSize(true);
        scheduleLayoutManager = new LinearLayoutManager(this);
        scheduleRecyclerView.setLayoutManager(scheduleLayoutManager);
        scheduleRecyclerView.addItemDecoration(new BorderLineDividerItemDecoration(this));
    }


    @Override
    public void onItemClickRequestsList(final int position, final int hour) {
        updateLesson = allLessons.get(hour).get(position);
        AlertDialog alertDialog = new AlertDialog.Builder(TeacherScheduleActivity.this).create();
        alertDialog.setTitle(getString(R.string.alert_dialog_student_request_lesson_title));
        alertDialog.setMessage(getString(R.string.alert_dialog_student_request_lesson_message));
        if(updateLesson.getConformationStatus() != Lesson.Status.T_CONFIRMED) {
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.confirm_lesson), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (oneRequestApprove == true) {
                        Toast.makeText(getApplicationContext(), getString(R.string.cant_approve_more_than_one_request), Toast.LENGTH_SHORT).show();
                    }else{
                        if(updateLesson.getConformationStatus() == Lesson.Status.T_UPDATE){
                            Toast.makeText(getApplicationContext(),getString(R.string.wait_for_student_response), Toast.LENGTH_SHORT).show();
                        } else {
                            updateStatus = Lesson.Status.T_CONFIRMED;
                            updateLesson.setConformationStatus(updateStatus);
                            oneRequestApprove = true;
                            requestsRecyclerView.findViewHolderForLayoutPosition(position).itemView.setBackgroundColor(Color.GREEN);
                            db.collection("lessons").document(updateLesson.getLessonID()).update("conformationStatus", updateStatus.toString());
                            allLessons.get(hour).set(position, updateLesson);
                            Toast.makeText(getApplicationContext(), "Lesson confirmed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        if(updateLesson.getConformationStatus() != Lesson.Status.T_CANCELED) {
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.reject_lesson), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (updateLesson.getConformationStatus() == Lesson.Status.T_CONFIRMED) {
                        oneRequestApprove = false;
                    }
                    updateStatus = Lesson.Status.T_CANCELED;
                    updateLesson.setConformationStatus(updateStatus);
                    requestsRecyclerView.findViewHolderForLayoutPosition(position).itemView.setBackgroundColor(Color.RED);
                    db.collection("lessons").document(updateLesson.getLessonID()).update("conformationStatus", updateStatus.toString());
                    allLessons.get(hour).set(position, updateLesson);
                    Toast.makeText(getApplicationContext(), "Lesson rejected", Toast.LENGTH_SHORT).show();
                }
            });
            alertDialog.show();
        }


    }

    @Override
    public void onEditButtonClickRequestsList(final int position,final int hour) {
        updateLesson = allLessons.get(hour).get(position);

        updateRequests = new Dialog(this);
        updateRequests.setContentView(R.layout.teacher_update_lesson_request);

        studentNameUpdateDialog = (TextView) updateRequests.findViewById(R.id.student_name);
        dateUpdateDialog = (TextView) updateRequests.findViewById(R.id.lesson_date);
        hourUpdateDialog = (TextView) updateRequests.findViewById(R.id.starting_time);
        startingLocationUpdateDialog = (TextView) updateRequests.findViewById(R.id.setStartingLocation);
        endingLocationUpdateDialog = (TextView) updateRequests.findViewById(R.id.setEndingLocation);
        update = (Button) updateRequests.findViewById(R.id.update_lesson);
        timePicker = (Button) updateRequests.findViewById(R.id.time_picker);
        datePicker = (Button) updateRequests.findViewById(R.id.date_picker);

        db.collection("Students").whereEqualTo("id", updateLesson.getStudentUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Student s = document.toObject(Student.class);
                                studentNameUpdateDialog.setText(s.getFirstName() + " " + s.getLastName());
                            }
                        }
                    }
                });
        dateUpdateDialog.setText(updateLesson.getDate());
        hourUpdateDialog.setText(updateLesson.getHour());
        startingLocationUpdateDialog.setText(updateLesson.getStartingLocation());
        endingLocationUpdateDialog.setText(updateLesson.getEndingLocation());
        updateRequests.show();
        //edit starting time for the lesson
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(), "time picker");
            }
        });
        //edit date for the lesson
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        TeacherScheduleActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        updateRequestsDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        updateRequestsDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                date = day + "/" + month + "/" + year;
                dateUpdateDialog.setText(date);
            }
        };


        update.setOnClickListener(new View.OnClickListener() {
            String newDate;
            String newHour;
            @Override
            public void onClick(View view) {
                if(hourUpdateDialog.getText() == ""){
                    Toast.makeText(TeacherScheduleActivity.this,R.string.select_hour, Toast.LENGTH_SHORT).show();
                }else{
                    if(dateUpdateDialog.getText() == ""){
                        Toast.makeText(TeacherScheduleActivity.this,R.string.select_hour, Toast.LENGTH_SHORT).show();
                    }else{
                        newHour = hourUpdateDialog.getText().toString();
                        newDate =dateUpdateDialog.getText().toString();
                        db.collection("lessons").document(updateLesson.getLessonID()).update("date", newDate, "hour", newHour, "conformationStatus", Lesson.Status.T_UPDATE.toString());
                        requestsRecyclerView.findViewHolderForLayoutPosition(position).itemView.setBackgroundColor(Color.YELLOW);
                        allLessons.get(hour).remove(position);
                        updateRequests.hide();

                    }
                }
            }
        });

    }
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        if(minute>0){
            Toast.makeText(this,R.string.full_hour, Toast.LENGTH_SHORT).show();
        }else {
            if (hour > 20 || hour < 7) {
                Toast.makeText(this, R.string.full_hour, Toast.LENGTH_SHORT).show();
            } else {
                if (hour < 10) {
                    String fullHour = "0"+hour+":"+"00";
                    hourUpdateDialog.setText(fullHour);
                } else {
                    String fullHour = hour+":"+"00";
                    hourUpdateDialog.setText(fullHour);
                }
            }
        }
    }
    private void initializerequestsRecyclerView(){
        requestsRecyclerView = (RecyclerView) studentsRequests.findViewById(R.id.hours_list_requests_dialog);
        requestsRecyclerView.setHasFixedSize(true);
        requestsLayoutManager = new LinearLayoutManager(this);
        requestsRecyclerView.setLayoutManager(requestsLayoutManager);
        requestsRecyclerView.addItemDecoration(new BorderLineDividerItemDecoration(this));
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


    private void initializeLLessonsist(){
        list0 = new LinkedList<Lesson>();
        list1 = new LinkedList<Lesson>();
        list2 = new LinkedList<Lesson>();
        list3 = new LinkedList<Lesson>();
        list4 = new LinkedList<Lesson>();
        list5 = new LinkedList<Lesson>();
        list6 = new LinkedList<Lesson>();
        list7 = new LinkedList<Lesson>();
        list8 = new LinkedList<Lesson>();
        list9 = new LinkedList<Lesson>();
        list10 = new LinkedList<Lesson>();
        list11 = new LinkedList<Lesson>();
        list12 = new LinkedList<Lesson>();
        list13 = new LinkedList<Lesson>();
        allLessons = new ArrayList<LinkedList<Lesson>>();
        allLessons.add(0,list0);
        allLessons.add(1,list1);
        allLessons.add(2,list2);
        allLessons.add(3,list3);
        allLessons.add(4,list4);
        allLessons.add(5,list5);
        allLessons.add(6,list6);
        allLessons.add(7,list7);
        allLessons.add(8,list8);
        allLessons.add(9,list9);
        allLessons.add(10,list10);
        allLessons.add(11,list11);
        allLessons.add(12,list12);
        allLessons.add(13,list13);
    }

    private void updateLessons(){
        initializeLLessonsist();
        db.collection("lessons").whereEqualTo("teacherUID", mTeacher.getID()).whereEqualTo("date",date)
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
                                if(hoursStatus[i] != Lesson.Status.S_CONFIRMED && hoursStatus[i] != Lesson.Status.S_CONFIRMED){
                                    hoursStatus[i] = l.getConformationStatus();
                                }
                                allLessons.get(i).add(l);
                            }
                            scheduleAdapter = new TeacherScheduleAdapter(TeacherScheduleActivity.this, hoursStatus,allLessons);
                            ((TeacherScheduleAdapter) scheduleAdapter)
                                    .setOnItemClickListener(TeacherScheduleActivity.this);
                            scheduleRecyclerView.setAdapter(scheduleAdapter);
                        }
                    }
                });
    }

    private void closingStudentsRequestsDialog(int position){
        if(oneRequestApprove == true){
            for(Lesson l: allLessons.get(position)){
                if(l.getConformationStatus() == Lesson.Status.S_REQUEST || l.getConformationStatus() == Lesson.Status.S_UPDATE ||
                        l.getConformationStatus() == Lesson.Status.S_CONFIRMED || l.getConformationStatus() == Lesson.Status.T_UPDATE ){
                    Toast.makeText(TeacherScheduleActivity.this,"DELETE OR UPDATE LESSONS", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        updateDayView();
        studentsRequests.hide();
    }


    private void setCurrentDay(){
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) +1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        date = currentDay + "/" + currentMonth + "/" + currentYear;
        dateSelected.setText(date);
        updateDayView();

    }

}
