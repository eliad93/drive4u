package com.base.eliad.drive4u.student_ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.adapters.StudentScheduleAdapter;
import com.base.eliad.drive4u.base_activities.StudentBaseActivity;
import com.base.eliad.drive4u.built_in_utils.BorderLineDividerItemDecoration;
import com.base.eliad.drive4u.models.Lesson;
import com.base.eliad.drive4u.models.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


public class StudentSchedulerLessonActivity extends StudentBaseActivity
        implements StudentScheduleAdapter.OnItemClickListener {

    //select date
    private TextView selectedDate;
    private TextView dateSelected;
    private TextView previousDay;
    private TextView nextDay;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Calendar cal;
    private String date;

    // RecyclerView for lesson to choose from
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //saving list of lessons from the DB and their status
    private List<Lesson> lessons;
    private List<Lesson> lessonsToRemove;
    private List<Lesson> lessonsSort;
    private List<Lesson> lessonsTemp;
    private List<String> relevantHours;
    //The teacher of the current student
    Teacher mTeacher;

    //create new lesson request
    private Button submit;
    private TextView lessonStartingTime;
    private TextView lessonEndingTime;
    private TextView lessonStartingLocation;
    private TextView lessonEndingLocation;
    private TextView lessonDate;
    private Dialog lessonCreate;
    private String hourChoose;
    private Lesson lessonChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_scheduler_lesson);
        cal = Calendar.getInstance();

        db.collection("Teachers")
                .document(mStudent.getTeacherId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        mTeacher = documentSnapshot.toObject(Teacher.class);

                        initializeRecyclerView();

                        dateSelected = findViewById(R.id.date_selected);
                        selectedDate = findViewById(R.id.selecte_date);
                        previousDay = findViewById(R.id.previous_date);
                        nextDay = findViewById(R.id.next_date);
                        setCurrentDay();
                        selectedDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int year = cal.get(Calendar.YEAR);
                                int month = cal.get(Calendar.MONTH);
                                int day = cal.get(Calendar.DAY_OF_MONTH);
                                DatePickerDialog dialog = new DatePickerDialog(
                                        StudentSchedulerLessonActivity.this,
                                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                        mDateSetListener,
                                        year, month, day);
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StudentSchedulerLessonActivity.this,"Cant find teacher",Toast.LENGTH_SHORT).show();
                        myStartActivity(StudentMainActivity.class);
                    }
                });
    }


    private void initializeRecyclerView() {
        mRecyclerView = findViewById(R.id.lessons_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.clearOnChildAttachStateChangeListeners();
        mRecyclerView.addItemDecoration(new BorderLineDividerItemDecoration(this));
    }

    private void setCurrentDay() {
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        date = currentDay + "/" + currentMonth + "/" + currentYear;
        dateSelected.setText(date);
        updateDayView();

    }

    private void updateDayView() {
        lessons = new LinkedList<>();
        lessonsToRemove = new LinkedList<>();
        lessonsSort = new LinkedList<>();
        lessonsTemp = new LinkedList<>();
        relevantHours = new LinkedList<>();
        //db.collection("lessons").whereEqualTo("teacherUID", mStudent.getTeacherId()).whereEqualTo("date", date).orderBy("hour")
        db.collection("lessons").whereEqualTo("teacherUID", mStudent.getTeacherId()).whereEqualTo("date", date)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Lesson lesson = document.toObject(Lesson.class);
                                if(lesson.getConformationStatus() == Lesson.Status.T_UPDATE || lesson.getConformationStatus() == Lesson.Status.S_CONFIRMED || lesson.getConformationStatus() == Lesson.Status.S_REQUEST
                                        ||lesson.getConformationStatus() == Lesson.Status.S_UPDATE || lesson.getConformationStatus() == Lesson.Status.T_OPTION){
                                    lessons.add(lesson);
                                }else{
                                    //remove the lesson that canceled for current student
                                    if((lesson.getConformationStatus() == Lesson.Status.T_CANCELED || lesson.getConformationStatus() == Lesson.Status.S_CANCELED)&& lesson.getStudentUID().equals(mStudent.getID())){
                                        lessonsToRemove.add(lesson);
                                    }
                                }
                            }

                            for (int i = 0; i < lessons.size() - 1; i++) {
                                for (int j = 0; i < lessonsToRemove.size() - 1; i++) {
                                    if(lessons.get(i).getHour().equals(lessonsToRemove.get(j).getHour())){
                                        lessons.remove(i);
                                    }
                                }
                            }

                            lessonsTemp.addAll(lessons);
                            //order by hours
                            for(int i=0; i<lessons.size(); i++){
                                Lesson min = lessonsTemp.get(0);
                                for(int j=0; j<lessonsTemp.size();j++){
                                    if(hourLowerThan(lessonsTemp.get(j).getHour(),min.getHour())){
                                        min = lessonsTemp.get(j);
                                    }
                                }
                                lessonsTemp.remove(min);
                                lessonsSort.add(min);
                            }

                            for(Lesson l:lessonsSort){
                                boolean hour_added = false;
                                for(String s:relevantHours){
                                    if(s.equals(l.getHour())){
                                        hour_added = true;
                                        break;
                                    }
                                }
                                if(!hour_added){
                                    relevantHours.add(l.getHour());
                                }

                            }

                            mAdapter = new StudentScheduleAdapter(StudentSchedulerLessonActivity.this,relevantHours, lessonsSort, mTeacher.getLessonLength());
                            ((StudentScheduleAdapter) mAdapter)
                                    .setOnItemClickListener(StudentSchedulerLessonActivity.this);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    }
                });
    }

    @Override
    public void onItemClick(final int position, List<Lesson> lessonsView) {
        //lesson that the curent student schedule
        lessonChoose = lessonsView.get(0);
        Lesson ExictingRequest = lessonsView.get(0);
        boolean alreayAskedThisHour = false;
        hourChoose = relevantHours.get(position);
        for(Lesson l:lessonsView){
            if (l.getStudentUID().equals(mStudent.getID())) {
                ExictingRequest = l;
                alreayAskedThisHour = true;
                break;
            }
        }
        if (alreayAskedThisHour) {
            if (ExictingRequest.getConformationStatus() != Lesson.Status.T_OPTION || ExictingRequest.getStudentUID().equals(mStudent.getID())) {
                if (ExictingRequest.getConformationStatus() == Lesson.Status.S_REQUEST) {
                    Toast.makeText(this, R.string.already_schedule_this_hour, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ExictingRequest.getConformationStatus() == Lesson.Status.T_UPDATE) {
                    Toast.makeText(this, R.string.teacher_update_lesson_to_hour, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ExictingRequest.getConformationStatus() == Lesson.Status.S_UPDATE) {
                    Toast.makeText(this, R.string.wait_for_teacher_response, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ExictingRequest.getConformationStatus() == Lesson.Status.S_CONFIRMED) {
                    Toast.makeText(this, R.string.wait_for_teacher_response, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ExictingRequest.getConformationStatus() == Lesson.Status.T_CONFIRMED) {
                    Toast.makeText(this, R.string.hour_is_taken, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }else {
                mRecyclerView.findViewHolderForAdapterPosition(position).itemView.setBackgroundColor(this.getResources().getColor(R.color.lessonApprove));
                lessonCreate = new Dialog(this);
                lessonCreate.setContentView(R.layout.student_schedule_lesson_dialog);

                lessonStartingTime = lessonCreate.findViewById(R.id.starting_time);
                lessonEndingTime = lessonCreate.findViewById(R.id.ending_time);
                lessonStartingLocation = lessonCreate.findViewById(R.id.starting_location);
                lessonEndingLocation = lessonCreate.findViewById(R.id.ending_location);
                lessonDate = lessonCreate.findViewById(R.id.lesson_date);
                submit = lessonCreate.findViewById(R.id.add_lesson);

                lessonStartingTime.setText(relevantHours.get(position));
                lessonEndingTime.setText(calculateTime(relevantHours.get(position), mTeacher.getLessonLength()));
                lessonDate.setText(date);

                lessonCreate.show();
                lessonCreate.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Lesson.Status prevStatus = lessonChoose.getConformationStatus();
                        if (prevStatus == Lesson.Status.T_OPTION) {
                            mRecyclerView.findViewHolderForAdapterPosition(position).itemView.setBackgroundColor(StudentSchedulerLessonActivity.this.getResources().getColor(R.color.lessonFree));
                        } else {
                            mRecyclerView.findViewHolderForAdapterPosition(position).itemView.setBackgroundColor(StudentSchedulerLessonActivity.this.getResources().getColor(R.color.lessonHaveRequests));
                        }
                    }
                });
                submit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        addLessonRequest(lessonChoose);
                        updateDayView();
                    }
                });
            }
        }


    public void addLessonRequest(Lesson l) {
        Lesson lesson = new Lesson(mStudent.getTeacherId(), mStudent.getID(), date, lessonStartingTime.getText().toString(),
                "1", "1", Lesson.Status.S_REQUEST);

        db.collection("lessons").add(lesson).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    String id = task.getResult().getId();
                    db.collection("lessons").document(id).update("lessonID", id);
                    Toast.makeText(StudentSchedulerLessonActivity.this, R.string.request_sent, Toast.LENGTH_SHORT).show();
                    lessonCreate.hide();
                }
            }
        });
        if(l.getConformationStatus() == Lesson.Status.T_OPTION){
            db.collection("lessons").document(l.getLessonID()).delete();
        }
    }
    private String calculateTime(String hour, int addition) {
        String[] hourMinutes = hour.split(":");
        int time = Integer.parseInt(hourMinutes[0]) * 60 + Integer.parseInt(hourMinutes[1]) + addition;
        int newHour = time / 60;
        int newMinutes = time % 60;
        if (newMinutes < 10) {
            return String.valueOf(newHour) + ":" + "0" + String.valueOf(newMinutes);
        }
        return String.valueOf(newHour) + ":" + String.valueOf(newMinutes);
    }

    private boolean hourLowerThan(String hour1, String hour2){
        //return true if hour1 is before hour2
        int time1 = Integer.valueOf(hour1.split(":")[0]) * 60 + Integer.valueOf(hour1.split(":")[1]);
        int time2 = Integer.valueOf(hour2.split(":")[0]) * 60 + Integer.valueOf(hour2.split(":")[1]);

        return time1 <= time2;
    }

}
