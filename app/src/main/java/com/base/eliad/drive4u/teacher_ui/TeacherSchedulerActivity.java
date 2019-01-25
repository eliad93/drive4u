package com.base.eliad.drive4u.teacher_ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.adapters.TeacherChooseStudentLessonAdapter;
import com.base.eliad.drive4u.base_activities.TeacherBaseActivity;
import com.base.eliad.drive4u.base_activities.TeacherBaseNavigationActivity;
import com.base.eliad.drive4u.built_in_utils.BorderLineDividerItemDecoration;
import com.base.eliad.drive4u.models.Lesson;
import com.base.eliad.drive4u.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class TeacherSchedulerActivity extends TeacherBaseNavigationActivity
        implements TeacherChooseStudentLessonAdapter.OnItemClickListener{
    private static final String TAG = TeacherSchedulerActivity.class.getSimpleName();
    private RelativeLayout mLayout;
    private RelativeLayout.LayoutParams lParam;
    private ScrollView mScroll;
    private float dpi;
    boolean canAdd;
    //for updating lesson time (location on screen)
    private float yMove;
    private float yStart;
    private int marginStart;
    private boolean moved = false;
    String prevStarting="";
    int prevMargin = 0;
    int prevColor;
    TextView prev = null;
    //select date
    private TextView selectedDate;
    private TextView dateSelected;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Calendar cal;
    private String date;
    //students requests for hour
    private Dialog studentsRequests;
    private RecyclerView requestsRecyclerView; //recyclerView for students requests
    private RecyclerView.Adapter requestsAdapter;
    private RecyclerView.LayoutManager requestsLayoutManager;
    private TextView dateDialog;
    private TextView hourDialog;
    private Button finishButton;
    private boolean oneRequestApprove = false;
    Lesson updateLesson;
    Lesson.Status updateStatus;
    //teacher update student request for lesson dialog
    private Dialog updateRequests;
    private TextView dateUpdateDialog;
    private TextView studentNameUpdateDialog;
    private TextView startingLocationUpdateDialog;
    private TextView endingLocationUpdateDialog;
    private Button update;
    private Spinner spinner;
    private Button datePicker;
    private String updateDate;
    private List<String> freeLessonsInDate;
    private String[] freeLessonsSortedInDate;
    private List<Lesson> updatedLessons;
    private DatePickerDialog.OnDateSetListener updateRequestsDateSetListener;
    //list of vies that added to current Layout
    private List<TextView> addedViews;
    //list of lessons from database
    private List<Lesson> lessons;
    //list of hours that have lessons
    private List<String> takenHours;
    //free lessons in current day
    private List<String> freeLessons;
    private String[] freeLessonsSorted;
    private List<String> lessonsTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_scheduler);
        canAdd = false;
        mLayout = (RelativeLayout) findViewById(R.id.event_column);
        mScroll = (ScrollView) findViewById(R.id.scroll_view);
        dpi = getResources().getDisplayMetrics().density;
        addedViews = new LinkedList<>();
        cal = Calendar.getInstance();

        dateSelected = (TextView) findViewById(R.id.date_selected);
        selectedDate = (TextView) findViewById(R.id.selecte_date);

        setCurrentDay();
        selectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        TeacherSchedulerActivity.this,
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

    private void updateDayView(){
        moved = false;
        canAdd = false;
        oneRequestApprove = false;
        //delete added views
        for(int i=0;i<addedViews.size();i++){
            mLayout.removeView(addedViews.get(i));
        }
        resetTags();
        prevStarting="";
        prevMargin = 0;
        prev = null;
        oneRequestApprove = false;
        addedViews = new LinkedList<>();
        //showing saved lessons
        showingSchedulsLessons();
        canAdd = true;
    }

    private void setCurrentDay(){
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) +1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        date = currentDay + "/" + currentMonth + "/" + currentYear;
        dateSelected.setText(date);
        updateDayView();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addNewLesson(View v) {
        if(!canAdd){
            return;
        }
        //new lesson parameters
        int lessonLen = mTeacher.getLessonLength();
        //handle with the previous lesson, check if saved or not
        handlePreviousView();
        String startingTime = findFreeSpace(v, lessonLen);
        if (startingTime.equals("")) {
            Toast.makeText(TeacherSchedulerActivity.this, "This hour is full", Toast.LENGTH_SHORT).show();
            return;
        }

        int hours = Integer.valueOf(startingTime.split(":")[0]);
        int minutes = Integer.valueOf(startingTime.split(":")[1]);
        int newLessonHeight = (int) (v.getHeight() * ((double) lessonLen / 60));
        int newLessonWidth = v.getWidth();
        int newLessonMarginTop = (int) (v.getHeight() * (hours - 5) + v.getHeight() * ((double) minutes / 60));
        String endingTime = calculateTime(startingTime, lessonLen);
        //create new lesson textView
        final TextView newLessonView = new TextView(this);
        final RelativeLayout.LayoutParams lParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lParam.topMargin = newLessonMarginTop + 1; //saving space between lessons textViews
        newLessonView.setLayoutParams(lParam);
        newLessonView.setPadding(0, 0, 0, 0);
        newLessonView.setHeight(newLessonHeight - 2); //saving space between lessons textViews
        newLessonView.setWidth(newLessonWidth);
        newLessonView.setGravity(Gravity.LEFT);
        newLessonView.setText(startingTime +"-"+ endingTime +", " + "press again for saving");
        newLessonView.setTextColor(this.getResources().getColor(R.color.colorBlack));
        newLessonView.setTextSize(10);
        newLessonView.setTag("unsaved");
        newLessonView.setBackgroundColor(this.getResources().getColor(R.color.lessonUnsaved));
        newLessonView.setClickable(true);
        mLayout.addView(newLessonView);
        freeLessons.add(startingTime);
        addedViews.add(newLessonView);
        prev = newLessonView;
        prevStarting = startingTime;
        prevMargin = lParam.topMargin;
        //addingToTag(v, startingTime, endingTime);
        newLessonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!moved) {
                    //entering to update mode
                    final RelativeLayout.LayoutParams lParam = (RelativeLayout.LayoutParams) newLessonView.getLayoutParams();
                    if (!newLessonView.getText().toString().contains(",")) {
                        //delete or update message
                        AlertDialog alertDialog = new AlertDialog.Builder(TeacherSchedulerActivity.this).create();
                        alertDialog.setTitle(getString(R.string.edit_lesson));
                        alertDialog.setMessage(getString(R.string.update_or_delete_message));
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.update), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removeFromTag(findViewById(getRowIdFromMargin(lParam.topMargin)),newLessonView.getText().toString().split("-")[0],newLessonView.getText().toString().split("-")[1]);
                                freeLessons.remove(newLessonView.getText().toString().split("-")[0]);
                                newLessonView.setText(newLessonView.getText() + ", press again for saving");
                                newLessonView.setTag("update");
                                prev = newLessonView;
                                prevStarting = newLessonView.getText().toString().split("-")[0];
                                prevMargin = lParam.topMargin;
                                newLessonView.setBackgroundColor(TeacherSchedulerActivity.this.getResources().getColor(R.color.lessonUnsaved));
                            }
                        });

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.delete), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //delete from database
                                removeFromTag(findViewById(getRowIdFromMargin(lParam.topMargin)),newLessonView.getText().toString().split("-")[0],newLessonView.getText().toString().split("-")[1]);
                                deleteLessonInDB(newLessonView.getText().toString().split("-")[0], "");
                                freeLessons.remove(newLessonView.getText().toString().split("-")[0]);
                                mLayout.removeView(newLessonView);
                                addedViews.remove(newLessonView);
                                Toast.makeText(TeacherSchedulerActivity.this,R.string.lesson_deleted, Toast.LENGTH_SHORT).show();
                            }
                        });
                        alertDialog.show();

                    } else {
                        String time = newLessonView.getText().toString().split(",")[0];
                        if (newLessonView.getTag().toString().equals("unsaved")) {
                            //adding to database
                            addLessonToDB(time.split("-")[0]);
                            addingToTag(findViewById(getRowIdFromMargin(lParam.topMargin)), time.split("-")[0], time.split("-")[1]);
                            freeLessons.add(time.split("-")[0]);
                        } else {
                            if (newLessonView.getTag().toString().equals("update")) {
                                //update in database
                                updateLessonInDB(prevStarting, time.split("-")[0], "");
                                addingToTag(findViewById(getRowIdFromMargin(lParam.topMargin)), time.split("-")[0], time.split("-")[1]);
                                freeLessons.add(time.split("-")[0]);
                            }
                        }

                        newLessonView.setText(time);
                        newLessonView.setTag("saved");
                        newLessonView.setBackgroundColor(TeacherSchedulerActivity.this.getResources().getColor(R.color.lessonFree));
                        prev = newLessonView;
                    }
                }else{
                    moved = false;
                }
            }
        });

        newLessonView.setOnTouchListener(new View.OnTouchListener() {
            //save textview old parameters
            String time;
            String starting;
            String ending;
            final RelativeLayout.LayoutParams lParam = (RelativeLayout.LayoutParams) newLessonView.getLayoutParams();
            int id;
            TextView prevHour;
            //parameters for updating
            int addition;
            String newStartingHour="", newEndingHour="";

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!newLessonView.getText().toString().contains(",")){
                    return false;
                }
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time = newLessonView.getText().toString().split(",")[0];
                        starting = time.split("-")[0];
                        ending = time.split("-")[1];
                        id = getRowIdFromMargin(lParam.topMargin);
                        prevHour = findViewById(id);
                        //parameters for updating
                        addition = 0;
                        yStart = motionEvent.getY();
                        mScroll.requestDisallowInterceptTouchEvent(true);
                        marginStart = lParam.topMargin;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moved = true;
                        int prevMargin =  lParam.topMargin;
                        yMove = motionEvent.getY();
                        if(lParam.topMargin+((yMove - yStart) / dpi)>=0 && lParam.topMargin+((yMove - yStart) / dpi)<=1100*dpi) {
                            lParam.topMargin += ((yMove - yStart) / dpi);
                            newLessonView.setLayoutParams(lParam);
                        }
                        if (Math.abs((lParam.topMargin - prevMargin) / dpi) >= 5) {
                            addition = ((int) Math.floor(((lParam.topMargin - marginStart) / dpi)) / 5) * 5;
                            newStartingHour = calculateTime(starting, addition);
                            newEndingHour = calculateTime(ending, addition);
                            newLessonView.setText(String.format("%s%s%s", newStartingHour, "-", newEndingHour + ", " + "press again for saving"));
                        }else{
                            lParam.topMargin = prevMargin;
                        }
                        newLessonView.setLayoutParams(lParam);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(!starting.equals(newStartingHour) && !newStartingHour.equals("")) {
                            //removeFromTag(prevHour, starting, ending);
                            lParam.topMargin = (int) (marginStart + addition * dpi);
                            id = getRowIdFromMargin(lParam.topMargin);
                            TextView current = findViewById(id);
                            //check if the lesson can move two the new location,
                            //if the new location is in hour 23:00 so denied (cant start lesson in the last hour
                            if (checkIfCanMove(current, newStartingHour, newEndingHour) && id != R.id.time_23) {
                                //addingToTag(current, newStartingHour, newEndingHour);
                                newLessonView.setLayoutParams(lParam);
                            } else {
                                Toast.makeText(TeacherSchedulerActivity.this, "Cant move lesson to this hour", Toast.LENGTH_SHORT).show();
                                //addingToTag(prevHour, starting, ending);
                                newLessonView.setText(time + ", press again for saving");
                                lParam.topMargin = marginStart;
                                newLessonView.setLayoutParams(lParam);
                            }
                        }
                        mScroll.requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void CreateFreeLesson(View v, String startingTime){
        int lessonLen = mTeacher.getLessonLength();
        int hours = Integer.valueOf(startingTime.split(":")[0]);
        int minutes = Integer.valueOf(startingTime.split(":")[1]);
        int newLessonHeight = (int) (v.getHeight() * ((double) lessonLen / 60));
        int newLessonWidth = v.getWidth();
        int newLessonMarginTop = (int) (v.getHeight() * (hours - 5) + v.getHeight() * ((double) minutes / 60));
        String endingTime = calculateTime(startingTime, lessonLen);
        //create new lesson textView
        final TextView newLessonView = new TextView(this);
        final RelativeLayout.LayoutParams lParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lParam.topMargin = newLessonMarginTop + 1; //saving space between lessons textViews
        newLessonView.setLayoutParams(lParam);
        newLessonView.setPadding(0, 0, 0, 0);
        newLessonView.setHeight(newLessonHeight - 2); //saving space between lessons textViews
        newLessonView.setWidth(newLessonWidth);
        newLessonView.setGravity(Gravity.LEFT);
        newLessonView.setText(startingTime +"-"+ endingTime);
        newLessonView.setTextColor(this.getResources().getColor(R.color.colorBlack));
        newLessonView.setTextSize(10);
        newLessonView.setTag("saved");
        newLessonView.setBackgroundColor(this.getResources().getColor(R.color.lessonFree));
        newLessonView.setClickable(true);
        mLayout.addView(newLessonView);
        addedViews.add(newLessonView);
        freeLessons.add(startingTime);
        prev = newLessonView;
        prevStarting = startingTime;
        prevMargin = lParam.topMargin;
        addingToTag(v, startingTime, endingTime);

        newLessonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!moved) {
                    //entering to update mode
                    final RelativeLayout.LayoutParams lParam = (RelativeLayout.LayoutParams) newLessonView.getLayoutParams();
                    if (!newLessonView.getText().toString().contains(",")) {
                        //delete or update message
                        AlertDialog alertDialog = new AlertDialog.Builder(TeacherSchedulerActivity.this).create();
                        alertDialog.setTitle(getString(R.string.edit_lesson));
                        alertDialog.setMessage(getString(R.string.update_or_delete_message));
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.update), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                newLessonView.setText(newLessonView.getText() + ", press again for saving");
                                newLessonView.setTag("update");
                                prev = newLessonView;
                                prevStarting = newLessonView.getText().toString().split("-")[0];
                                prevMargin = lParam.topMargin;
                                prevColor = R.color.lessonFree;
                                newLessonView.setBackgroundColor(TeacherSchedulerActivity.this.getResources().getColor(R.color.lessonUnsaved));
                            }
                        });

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.delete), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //delete from database
                                removeFromTag(findViewById(getRowIdFromMargin(lParam.topMargin)),newLessonView.getText().toString().split("-")[0],newLessonView.getText().toString().split("-")[1]);
                                deleteLessonInDB(newLessonView.getText().toString().split("-")[0], "");
                                mLayout.removeView(newLessonView);
                                addedViews.remove(newLessonView);
                                freeLessons.remove(newLessonView.getText().toString().split("-")[0]);
                                Toast.makeText(TeacherSchedulerActivity.this,R.string.lesson_deleted, Toast.LENGTH_SHORT).show();
                            }
                        });
                        alertDialog.show();
                    } else {
                        String time = newLessonView.getText().toString().split(", ")[0];
                        if (newLessonView.getTag().toString().equals("update")) {
                            //update in database
                            updateLessonInDB(prevStarting, time.split("-")[0], "");
                            freeLessons.remove(prevStarting);
                            freeLessons.add(time.split("-")[0]);
                            addingToTag(findViewById(getRowIdFromMargin(lParam.topMargin)),time.split("-")[0],time.split("-")[1] );
                            newLessonView.setText(time);
                            newLessonView.setTag("saved");
                            newLessonView.setBackgroundColor(TeacherSchedulerActivity.this.getResources().getColor(R.color.lessonFree));
                            prev = newLessonView;
                        }
                    }
                }else{
                    moved = false;
                }
            }
        });

        newLessonView.setOnTouchListener(new View.OnTouchListener() {
            //save textview old parameters
            String time;
            String starting;
            String ending;
            final RelativeLayout.LayoutParams lParam = (RelativeLayout.LayoutParams) newLessonView.getLayoutParams();
            int id;
            TextView prevHour;
            //parameters for updating
            int addition;
            String newStartingHour="", newEndingHour="";

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!newLessonView.getText().toString().contains(",")){
                    return false;
                }
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time = newLessonView.getText().toString().split(",")[0];
                        starting = time.split("-")[0];
                        ending = time.split("-")[1];
                        id = getRowIdFromMargin(lParam.topMargin);
                        prevHour = findViewById(id);
                        //parameters for updating
                        addition = 0;
                        yStart = motionEvent.getY();
                        mScroll.requestDisallowInterceptTouchEvent(true);
                        marginStart = lParam.topMargin;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moved = true;
                        int prevMargin =  lParam.topMargin;
                        yMove = motionEvent.getY();
                        if(lParam.topMargin+((yMove - yStart) / dpi)>=0 && lParam.topMargin+((yMove - yStart) / dpi)<=1100*dpi) {
                            lParam.topMargin += ((yMove - yStart) / dpi);
                            newLessonView.setLayoutParams(lParam);
                        }
                        if (Math.abs((lParam.topMargin - prevMargin) / dpi) >= 5) {
                            addition = ((int) Math.floor(((lParam.topMargin - marginStart) / dpi)) / 5) * 5;
                            newStartingHour = calculateTime(starting, addition);
                            newEndingHour = calculateTime(ending, addition);
                            newLessonView.setText(String.format("%s%s%s", newStartingHour, "-", newEndingHour + ", " + "press again for saving"));
                        }else{
                            lParam.topMargin = prevMargin;
                        }
                        newLessonView.setLayoutParams(lParam);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(!starting.equals(newStartingHour) && !newStartingHour.equals("")) {
                            removeFromTag(prevHour, starting, ending);
                            lParam.topMargin = (int) (marginStart + addition * dpi);
                            id = getRowIdFromMargin(lParam.topMargin);
                            TextView current = findViewById(id);
                            //check if the lesson can move two the new location,
                            //if the new location is in hour 23:00 so denied (cant start lesson in the last hour
                            if (checkIfCanMove(current, newStartingHour, newEndingHour) && id != R.id.time_23) {
                                //addingToTag(current, newStartingHour, newEndingHour);
                                prevMargin = lParam.topMargin;
                                newLessonView.setLayoutParams(lParam);
                            } else {
                                Toast.makeText(TeacherSchedulerActivity.this, "Cant move lesson to this hour", Toast.LENGTH_SHORT).show();
                                addingToTag(prevHour, starting, ending);
                                newLessonView.setText(time + ", press again for saving");
                                lParam.topMargin = marginStart;
                                newLessonView.setLayoutParams(lParam);
                            }
                        }
                        mScroll.requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });
    }

    private void handlePreviousView() {
        int lessonLen = mTeacher.getLessonLength();
        if(prev!=null){
            moved = false;
            if(!prev.getTag().equals("saved")){
                if(prev.getTag().equals("unsaved")){
                    final RelativeLayout.LayoutParams lParam = (RelativeLayout.LayoutParams) prev.getLayoutParams();
                    //removeFromTag(findViewById(getRowIdFromMargin(lParam.topMargin)),prev.getText().toString().split("-")[0],prev.getText().toString().split("-")[1]);
                    mLayout.removeView(prev);
                    addedViews.remove(prev);
                }else{
                    final RelativeLayout.LayoutParams lParam = (RelativeLayout.LayoutParams) prev.getLayoutParams();
                    lParam.topMargin = prevMargin;
                    addingToTag(findViewById(getRowIdFromMargin(prevMargin)), prevStarting, prevStarting);
                    prev.setLayoutParams(lParam);
                    prev.setText(prevStarting+"-"+calculateTime(prevStarting,lessonLen));
                    prev.setBackgroundColor(TeacherSchedulerActivity.this.getResources().getColor(prevColor));
                }
            }
        }
    }

    private void addLessonToDB(String starting) {
        Lesson lesson = new Lesson(mTeacher.getID(), "", date,starting,
                "","", Lesson.Status.T_OPTION);
        db.collection("lessons").add(lesson).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    String id = task.getResult().getId();
                    db.collection("lessons").document(id).update("lessonID",id);
                    Toast.makeText(TeacherSchedulerActivity.this, getString(R.string.new_lesson_added), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void updateLessonInDB(String prevStartingHour, final String newStartingHour, String studentID) {
        db.collection("lessons").whereEqualTo("date", date).whereEqualTo("hour", prevStartingHour).whereEqualTo("studentUID", studentID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String id = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                id = document.getId();
                            }
                            db.collection("lessons").document(id).update("hour", newStartingHour);
                        }
                    }
                });
    }
    private void deleteLessonInDB(String startingHour, String studentID) {
        db.collection("lessons").whereEqualTo("date", date).whereEqualTo("hour", startingHour).whereEqualTo("studentUID", studentID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String id = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                id = document.getId();
                            }
                            db.collection("lessons").document(id).delete();
                        }
                    }
                });
    }

    private void updateLessonStatusInDB(Lesson lesson, final Lesson.Status newStatus) {
        db.collection("lessons").whereEqualTo("lessonID", lesson.getLessonID()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String id = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                id = document.getId();
                            }
                            db.collection("lessons").document(id).update("conformationStatus", newStatus.toString());
                        }
                    }
                });
    }

    private void addingToTag(View row, String starting, String ending) {
        String Tag = "";
        String newStart, newEnd;
        String[] free;
        if(row.getTag().toString().equals("")){
            free = new String[0];
        }else{
            free = row.getTag().toString().split(",");
        }
        //check if the lesson split between hours
        if (starting.split(":")[0].equals(ending.split(":")[0]) ||
                ending.split(":")[1].equals("00")) {
            for (int i = 0; i < free.length; i++) {
                String start = free[i].split("-")[0];
                String end = free[i].split("-")[1];
                //check if the lesson has space in the current free time - free[i]
                if (isBetweenHours(start, end, starting) && isBetweenHours(start, end, ending)) {
                    if (start.equals(starting) && end.equals(ending)) {
                        //the lesson fill exactly the free time
                        continue;
                    }
                    //the lesson starts exactly from the beginning of the free time but does not full it
                    //so there is a new free time between the lesson ending and the original free space ending
                    if (start.equals(starting)) {
                        if (Tag.equals("")) {
                            Tag = ending + "-" + end;
                        } else {
                            Tag = Tag + "," + ending + "-" + end;
                        }
                    } else {
                        //the lesson ends exactly in the ending of the free time but does not full it
                        //so there is a new free time between the lesson starting and the original free space starting
                        if (end.equals(ending)) {
                            if (Tag.equals("")) {
                                Tag = start + "-" + starting;
                            } else {
                                Tag = Tag + "," + start + "-" + starting;
                            }
                        } else {
                            //the lesson apply in the free time space, so two new free spaces are created, one between the original starting
                            //and the lesson starting time, and the second between the lesson ending and the original ending
                            if (Tag.equals("")) {
                                Tag = start + "-" + starting + "," + ending + "-" + end;
                            } else {
                                Tag = Tag + "," + start + "-" + starting + "," + ending + "-" + end;
                            }
                        }
                    }
                } else {
                    if (Tag.equals("")) {
                        Tag = free[i];
                    } else {
                        Tag = Tag + "," + free[i];
                    }
                }
            }
            row.setTag(Tag);
        } else {
            //the lesson is splitting between hours
            for (int i = 0; i < free.length - 1; i++) {
                if (Tag.equals("")) {
                    Tag = free[i];
                } else {
                    Tag = Tag + "," + free[i];
                }
            }
            String start = free[free.length - 1].split("-")[0];
            //new free space is created, if the condition is false so its means that the new lesson
            //full the last free space so it won't insert to the tag
            if (!start.equals(starting)) {
                if (Tag.equals("")) {
                    Tag = Tag + start + "-" + starting;
                } else {
                    Tag = Tag + "," + start + "-" + starting;
                }
            }
            String TagNext = "";
            TextView next = (TextView) findViewById(row.getNextFocusDownId());
            String[] freeNext;
            if(next.getTag().toString().equals("")){
                freeNext = new String[0];
            }else{
                freeNext = next.getTag().toString().split(",");
            }
            String endNext = freeNext[0].split("-")[1];
            //new free space is created, if the condition is false so its means that the new lesson
            //full the first free space so it won't insert to the tag
            if (!endNext.equals(ending)) {
                TagNext = ending + "-" + endNext;
            }
            for (int i = 1; i < freeNext.length; i++) {
                if (TagNext.equals("")) {
                    TagNext = freeNext[i];
                } else {
                    TagNext = TagNext + "," + freeNext[i];
                }
            }
            row.setTag(Tag);
            next.setTag(TagNext);
        }
    }

    private void removeFromTag(View row, String starting, String ending) {
        String baseStartingHour = starting.split(":")[0] + ":" + "00";
        String baseEndingHour = calculateTime(baseStartingHour, 60);
        String Tag = "";
        String[] free;
        if (row.getTag().toString().equals("")) {
            free = new String[0];
        } else {
            free = row.getTag().toString().split(",");
        }
        //check if the lesson is splitting between two hours
        if (starting.split(":")[0].equals(ending.split(":")[0]) ||
                ending.split(":")[1].equals("00")) {
            //all the hour is taken, free from starting to ending
            if (free.length == 0) {
                Tag = starting + "-" + ending;
            }
            //there is one free space in the hour
            if (free.length == 1) {
                String startingFreeSpace = free[0].split("-")[0];
                String endingFreeSpace = free[0].split("-")[1];
                //the free space is in the starting of the hour
                if (startingFreeSpace.equals(baseStartingHour)) {
                    //the lesson that deleted start right after the free space and united with it
                    if (starting.equals(endingFreeSpace)) {
                        Tag = startingFreeSpace + "-" + ending;
                    } else {
                        Tag = free[0] + "," + starting + "-" + ending; //the lesson that deleted is not united with the previous free space
                    }
                } else {
                    //the free space is in the ending of the hour
                    if (endingFreeSpace.equals(baseEndingHour)) {
                        //the lesson that deleted start right before the free space and united with it
                        if (ending.equals(startingFreeSpace)) {
                            Tag = starting + "-" + endingFreeSpace;
                        } else {
                            Tag = starting + "-" + ending + "," + free[0]; //the lesson that deleted is not united with the previous free space
                        }
                    } else {
                        //the free space is in the middle of the hour
                        //check if the deleted lesson is before or after the free space
                        if (isBetweenHours(baseStartingHour, startingFreeSpace, starting)) {
                            //check if united
                            if (ending.equals(startingFreeSpace)) {
                                Tag = starting + "-" + endingFreeSpace;
                            } else {
                                Tag = starting + "-" + ending + "," + free[0];
                            }
                        } else {
                            if (starting.equals(endingFreeSpace)) {
                                Tag = startingFreeSpace + "-" + ending;
                            } else {
                                Tag = free[0] + "," + starting + "-" + ending;
                            }
                        }
                    }
                }
            }
            //there is two free spaces in the hour
            if (free.length == 2) {
                String startingFreeSpace_1 = free[0].split("-")[0];
                String endingFreeSpace_1 = free[0].split("-")[1];
                String startingFreeSpace_2 = free[1].split("-")[0];
                String endingFreeSpace_2 = free[1].split("-")[1];
                //the lesson that deleted is united with the two other free space
                if (starting.equals(endingFreeSpace_1) && ending.equals(startingFreeSpace_2)) {
                    Tag = startingFreeSpace_1 + "-" + endingFreeSpace_2;
                } else {
                    //the lesson that deleted is united only with the first free space
                    if (starting.equals(endingFreeSpace_1)) {
                        Tag = startingFreeSpace_1 + "-" + ending + "," + free[1];
                    } else {
                        //the lesson that deleted is united only with the second free space
                        if (ending.equals(startingFreeSpace_2)) {
                            Tag = free[0] + "," + starting + "-" + endingFreeSpace_2;
                        } else {
                            //the lesson that deleted is not uniting with any of the free spaces
                            Tag = free[0] + "," + starting + "-" + ending + "," + free[1];
                        }
                    }
                }
            }
            row.setTag(Tag);
        } else {
            //the lesson is splitting between two hours
            TextView next = (TextView) findViewById(row.getNextFocusDownId());
            String TagNext = "";
            String baseStartingHourNext = baseEndingHour;
            String baseEndingHourNext = calculateTime(baseStartingHourNext, 60);
            String[] freeNext;
            if (next.getTag().toString().equals("")) {
                freeNext = new String[0];
            } else {
                freeNext = next.getTag().toString().split(",");
            }
            //handle the first hour
            //there is no free spaces in the first hour
            if (free.length == 0) {
                Tag = starting + "-" + baseEndingHour;
            } else {
                //there is free spaces in the first hour
                //united with the last free space
                if (starting.equals(free[free.length - 1].split("-")[1])) {
                    for (int i = 0; i < free.length - 1; i++) {
                        if (Tag.equals("")) {
                            Tag = free[i];
                        } else {
                            Tag = Tag + "," + free[i];
                        }
                    }
                    if(Tag.equals("")){
                        Tag = free[free.length - 1].split("-")[0] + "-" + baseEndingHour;
                    }else{
                        Tag = Tag + "," + free[free.length - 1].split("-")[0] + "-" + baseEndingHour;
                    }
                } else {
                    //is not unite with the last free space
                    for (int i = 0; i < free.length; i++) {
                        if (Tag.equals("")) {
                            Tag = free[i];
                        } else {
                            Tag = Tag + "," + free[i];
                        }
                    }
                    if(Tag.equals("")){
                        Tag = starting + "-" + baseEndingHour;
                    }else{
                        Tag = Tag + "," + starting + "-" + baseEndingHour;
                    }
                }
            }
            //handle the second hour
            //there is no free spaces in the second hour
            if (freeNext.length == 0) {
                TagNext = baseStartingHourNext + "-" + ending;
            } else {
                //there is free spaces in the second hour
                //united with the first free space
                if (ending.equals(freeNext[0].split("-")[0])) {
                    TagNext = baseStartingHourNext + "-" + freeNext[0].split("-")[1];
                    for (int i = 1; i < freeNext.length; i++) {
                        TagNext = TagNext + "," + freeNext[i];
                    }
                } else {
                    //is not unite with the first free space
                    TagNext = baseStartingHourNext + ending;
                    for (int i = 0; i < freeNext.length; i++) {
                        TagNext = TagNext + "," + freeNext[i];
                    }
                }
            }
            row.setTag(Tag);
            next.setTag(TagNext);
        }
    }

    //for scheduling new lesson, return free hour in the given row
    private String findFreeSpace(View row, int len) {
        if (!row.getTag().equals("")) {
            //the free array length is not 0 because we pass the previous condition
            String[] free = row.getTag().toString().split(",");
            TextView next = (TextView) findViewById(row.getNextFocusDownId());
            String[] freeNext;
            if(next.getTag().toString().equals("")){
                freeNext = new String[0];
            }else{
                freeNext = next.getTag().toString().split(",");
            }
            for (int i = 0; i < free.length; i++) {
                String start = free[i].split("-")[0];
                String end = free[i].split("-")[1];
                String checkingEnd = calculateTime(start, len);
                if (isBetweenHours(start, end, checkingEnd)) {
                    return start;
                }
            }
            if (freeNext.length != 0) {
                //the next hour must start in the beginning and the previous hour must ends in round hour
                if(freeNext[0].split("-")[0].split(":")[1].equals("00") && free[free.length - 1].split("-")[1].split(":")[1].equals("00")){
                    String start = free[free.length - 1].split("-")[0];
                    String end = freeNext[0].split("-")[1];
                    String checkingEnd = calculateTime(start, len);
                    if (isBetweenHours(start, end, checkingEnd)) {
                        return start;
                    }
                }
            }
        }
        return "";
    }

    //calculating new hour from given hour and time added
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

    //checks if the given hour is between start and end
    private boolean isBetweenHours(String start, String end, String hour) {
        String[] hourMinutes_start = start.split(":");
        int time_start = Integer.parseInt(hourMinutes_start[0]) * 60 + Integer.parseInt(hourMinutes_start[1]);

        String[] hourMinutes_end = end.split(":");
        int time_end = Integer.parseInt(hourMinutes_end[0]) * 60 + Integer.parseInt(hourMinutes_end[1]);

        String[] hourMinutes_hour = hour.split(":");
        int time_hour = Integer.parseInt(hourMinutes_hour[0]) * 60 + Integer.parseInt(hourMinutes_hour[1]);

        if (time_hour >= time_start && time_hour <= time_end) {
            return true;
        }
        return false;
    }


    //for update lesson hour (location on screen), checks if the new hour is free
    private boolean checkIfCanMove(View current, String start, String end) {
        //parameters of current hour
        String Tag = current.getTag().toString();
        String[] free;
        if(Tag.equals("")){
            free = new String[0];
        }else{
            free = Tag.split(",");
        }
        //check if the lesson is splitting on two hours
        if (start.split(":")[0].equals(end.split(":")[0]) ||
                end.split(":")[1].equals("00")) {
            for (int i = 0; i < free.length; i++) {
                if (isBetweenHours(free[i].split("-")[0], free[i].split("-")[1], start) &&
                        isBetweenHours(free[i].split("-")[0], free[i].split("-")[1], end)) {
                    return true;
                }
            }
        }else{
            //parameters of next hour
            TextView next = (TextView) findViewById(current.getNextFocusDownId());
            String TagNext = next.getTag().toString();
            String[] freeNext;
            if(TagNext.equals("")){
                //need to split but the next hour is full
                return false;
            }else{
                freeNext = TagNext.split(",");
            }
            if(!freeNext[0].split("-")[0].split(":")[1].equals("00") || !free[free.length-1].split("-")[1].split(":")[1].equals("00")){
                //the free space in the next hour does not starting from the beginning of the hour or the free
                //space in the previous hour does not reach to the end of the hour
                return false;
            }
            if(isBetweenHours(free[free.length-1].split("-")[0], freeNext[0].split("-")[1], end) &&
                    isBetweenHours(free[free.length-1].split("-")[0], freeNext[0].split("-")[1], start)){
                return true;
            }
        }
        return false;
    }

    private void showingSchedulsLessons(){
        lessons = new LinkedList<>();
        takenHours = new LinkedList<>();
        freeLessons = new LinkedList<>();
        db.collection("lessons").whereEqualTo("teacherUID", mTeacher.getID()).whereEqualTo("date", date)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Lesson lesson = document.toObject(Lesson.class);
                        if(lesson.getConformationStatus() == Lesson.Status.S_CANCELED || lesson.getConformationStatus() == Lesson.Status.T_CANCELED){
                            continue;
                        }
                        lessons.add(lesson);
                    }
                    //find all the relevant hours
                    for (int i = 0; i < lessons.size(); i++) {
                        String hour = lessons.get(i).getHour();
                        boolean hourInList = false;
                        for (int j = 0; j < takenHours.size(); j++) {
                            if (takenHours.get(j).equals(hour)) {
                                hourInList = true;
                                break;
                            }
                        }
                        if (!hourInList) {
                            takenHours.add(hour);
                        }
                    }
                    //iteration on all the hours that have lessons
                    for(int j = 0; j < takenHours.size(); j++){
                        //create TextView for every hour
                        List<Lesson> lessonsInHour = new LinkedList<>();
                        for(int i=0; i<lessons.size();i++){
                            //create a list of lessons in current hour
                            if(lessons.get(i).getHour().equals(takenHours.get(j))){
                                lessonsInHour.add(lessons.get(i));
                            }
                        }
                        addViewForCurrentHour(takenHours.get(j), lessonsInHour);
                    }

                }
            }
        });
    }

    private void addViewForCurrentHour(String hour, List<Lesson> lessonsInHour){
        //if the lesson in this hour is only an optional lesson
        if(lessonsInHour.get(0).getConformationStatus() == Lesson.Status.T_OPTION){
            int hours = Integer.valueOf(hour.split(":")[0]);
            int minutes = Integer.valueOf(hour.split(":")[1]);
            int margin = (int)(((hours-5)*60 + minutes)*dpi);
            CreateFreeLesson(findViewById(getRowIdFromMargin(margin)),hour);
        }
        //if the lesson in this hour had approve
        if(lessonsInHour.get(0).getConformationStatus() == Lesson.Status.T_CONFIRMED){
            int hours = Integer.valueOf(hour.split(":")[0]);
            int minutes = Integer.valueOf(hour.split(":")[1]);
            int margin = (int)(((hours-5)*60 + minutes)*dpi);
            CreateApproveLesson(findViewById(getRowIdFromMargin(margin)),hour, lessonsInHour.get(0));
        }
        //if this hour has requests
        if(lessonsInHour.get(0).getConformationStatus() == Lesson.Status.T_UPDATE ||
                lessonsInHour.get(0).getConformationStatus() == Lesson.Status.S_CONFIRMED ||
                lessonsInHour.get(0).getConformationStatus() == Lesson.Status.S_REQUEST||
                lessonsInHour.get(0).getConformationStatus() == Lesson.Status.S_UPDATE){
            int hours = Integer.valueOf(hour.split(":")[0]);
            int minutes = Integer.valueOf(hour.split(":")[1]);
            int margin = (int)(((hours-5)*60 + minutes)*dpi);
            CreateRequestsView(findViewById(getRowIdFromMargin(margin)),hour,lessonsInHour);
        }
    }

    private void CreateApproveLesson(View v, String startingTime, final Lesson lesson){
        int lessonLen = mTeacher.getLessonLength();
        int hours = Integer.valueOf(startingTime.split(":")[0]);
        int minutes = Integer.valueOf(startingTime.split(":")[1]);
        int newLessonHeight = (int) (v.getHeight() * ((double) lessonLen / 60));
        int newLessonWidth = v.getWidth();
        int newLessonMarginTop = (int) (v.getHeight() * (hours - 5) + v.getHeight() * ((double) minutes / 60));
        String endingTime = calculateTime(startingTime, lessonLen);
        //create new lesson textView
        final TextView newLessonView = new TextView(this);
        final RelativeLayout.LayoutParams lParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lParam.topMargin = newLessonMarginTop + 1; //saving space between lessons textViews
        newLessonView.setLayoutParams(lParam);
        newLessonView.setPadding(0, 0, 0, 0);
        newLessonView.setHeight(newLessonHeight - 2); //saving space between lessons textViews
        newLessonView.setWidth(newLessonWidth);
        newLessonView.setGravity(Gravity.LEFT);
        newLessonView.setText(startingTime +"-"+ endingTime);
        newLessonView.setTextColor(this.getResources().getColor(R.color.colorBlack));
        newLessonView.setTextSize(10);
        newLessonView.setTag("saved");
        newLessonView.setBackgroundColor(this.getResources().getColor(R.color.lessonApprove));
        newLessonView.setClickable(true);
        mLayout.addView(newLessonView);
        addedViews.add(newLessonView);
        addingToTag(v, startingTime, endingTime);

        newLessonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!moved) {
                    //entering to update mode
                    final RelativeLayout.LayoutParams lParam = (RelativeLayout.LayoutParams) newLessonView.getLayoutParams();
                    if (!newLessonView.getText().toString().contains(",")) {
                        //delete or update message
                        AlertDialog alertDialog = new AlertDialog.Builder(TeacherSchedulerActivity.this).create();
                        alertDialog.setTitle(getString(R.string.edit_lesson));
                        alertDialog.setMessage(getString(R.string.update_or_delete_message));
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.update), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removeFromTag(findViewById(getRowIdFromMargin(lParam.topMargin)), newLessonView.getText().toString().split("-")[0], newLessonView.getText().toString().split("-")[1]);
                                newLessonView.setText(newLessonView.getText() + ", press again for saving");
                                newLessonView.setTag("update");
                                prev = newLessonView;
                                prevStarting = newLessonView.getText().toString().split("-")[0];
                                prevMargin = lParam.topMargin;
                                prevColor = TeacherSchedulerActivity.this.getResources().getColor(R.color.lessonApprove);
                            }
                        });

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.delete), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //delete from database
                                updateLessonStatusInDB(lesson, Lesson.Status.T_CANCELED);
                                removeFromTag(findViewById(getRowIdFromMargin(lParam.topMargin)), newLessonView.getText().toString().split("-")[0], newLessonView.getText().toString().split("-")[1]);
                                mLayout.removeView(newLessonView);
                                addedViews.remove(newLessonView);
                                Toast.makeText(TeacherSchedulerActivity.this, R.string.lesson_deleted, Toast.LENGTH_SHORT).show();
                            }
                        });
                        alertDialog.show();
                    } else {
                        String time = newLessonView.getText().toString().split(", ")[0];
                        if (newLessonView.getTag().toString().equals("update")) {
                            updateLessonInDB(prevStarting, time.split("-")[0], lesson.getStudentUID());
                            updateLessonStatusInDB(lesson, Lesson.Status.T_UPDATE);
                            mLayout.removeView(newLessonView);
                            addedViews.remove(newLessonView);
                            Lesson l = db.collection("lessons").document(lesson.getLessonID()).get().getResult().toObject(Lesson.class);
                            if(l!=null){
                                List<Lesson> list = new LinkedList<>();
                                list.add(l);
                                CreateRequestsView(findViewById(getRowIdFromMargin(lParam.topMargin)), l.getHour(), list);
                            }
                        }
                    }
                }else{
                    moved = false;
                }
            }
        });

        newLessonView.setOnTouchListener(new View.OnTouchListener() {
            //save textview old parameters
            String time;
            String starting;
            String ending;
            final RelativeLayout.LayoutParams lParam = (RelativeLayout.LayoutParams) newLessonView.getLayoutParams();
            int id;
            TextView prevHour;
            //parameters for updating
            int addition;
            String newStartingHour="", newEndingHour="";

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!newLessonView.getText().toString().contains(",")){
                    return false;
                }
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time = newLessonView.getText().toString().split(",")[0];
                        starting = time.split("-")[0];
                        ending = time.split("-")[1];
                        id = getRowIdFromMargin(lParam.topMargin);
                        prevHour = findViewById(id);

                        //parameters for updating
                        addition = 0;
                        yStart = motionEvent.getY();
                        mScroll.requestDisallowInterceptTouchEvent(true);
                        marginStart = lParam.topMargin;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moved = true;
                        int prevMargin =  lParam.topMargin;
                        yMove = motionEvent.getY();
                        if(lParam.topMargin+((yMove - yStart) / dpi)>=0 && lParam.topMargin+((yMove - yStart) / dpi)<=1100*dpi) {
                            lParam.topMargin += ((yMove - yStart) / dpi);
                            newLessonView.setLayoutParams(lParam);
                        }
                        if (Math.abs((lParam.topMargin - prevMargin) / dpi) >= 5) {
                            addition = ((int) Math.floor(((lParam.topMargin - marginStart) / dpi)) / 5) * 5;
                            newStartingHour = calculateTime(starting, addition);
                            newEndingHour = calculateTime(ending, addition);
                            newLessonView.setText(String.format("%s%s%s", newStartingHour, "-", newEndingHour + ", " + "press again for saving"));
                        }else{
                            lParam.topMargin = prevMargin;
                        }
                        newLessonView.setLayoutParams(lParam);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(!starting.equals(newStartingHour) && !newStartingHour.equals("")) {
                            //removeFromTag(prevHour, starting, ending);
                            lParam.topMargin = (int) (marginStart + addition * dpi);
                            id = getRowIdFromMargin(lParam.topMargin);
                            TextView current = findViewById(id);
                            //check if the lesson can move two the new location,
                            //if the new location is in hour 23:00 so denied (cant start lesson in the last hour
                            if (checkIfCanMove(current, newStartingHour, newEndingHour) && id != R.id.time_23) {
                                //addingToTag(current, newStartingHour, newEndingHour);
                                newLessonView.setLayoutParams(lParam);
                            } else {
                                Toast.makeText(TeacherSchedulerActivity.this, "Cant move lesson to this hour", Toast.LENGTH_SHORT).show();
                                addingToTag(prevHour, starting, ending);
                                newLessonView.setText(time + ", press again for saving");
                                lParam.topMargin = marginStart;
                                newLessonView.setLayoutParams(lParam);
                            }
                        }
                        mScroll.requestDisallowInterceptTouchEvent(false);

                }
                return false;
            }
        });
    }

    private void CreateRequestsView(View v, String startingTime, final List<Lesson> lessonsList){
        final int lessonLen = mTeacher.getLessonLength();
        int hours = Integer.valueOf(startingTime.split(":")[0]);
        int minutes = Integer.valueOf(startingTime.split(":")[1]);
        int newLessonHeight = (int) (v.getHeight() * ((double) lessonLen / 60));
        int newLessonWidth = v.getWidth();
        int newLessonMarginTop = (int) (v.getHeight() * (hours - 5) + v.getHeight() * ((double) minutes / 60));
        String endingTime = calculateTime(startingTime, lessonLen);
        //create new lesson textView
        final TextView newLessonView = new TextView(this);
        final RelativeLayout.LayoutParams lParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lParam.topMargin = newLessonMarginTop + 1; //saving space between lessons textViews
        newLessonView.setLayoutParams(lParam);
        newLessonView.setPadding(0, 0, 0, 0);
        newLessonView.setHeight(newLessonHeight - 2); //saving space between lessons textViews
        newLessonView.setWidth(newLessonWidth);
        newLessonView.setGravity(Gravity.LEFT);
        newLessonView.setText(startingTime +"-"+ endingTime);
        newLessonView.setTextColor(this.getResources().getColor(R.color.colorBlack));
        newLessonView.setTextSize(10);
        newLessonView.setTag("saved");
        newLessonView.setBackgroundColor(this.getResources().getColor(R.color.lessonHaveRequests));
        newLessonView.setClickable(true);
        mLayout.addView(newLessonView);
        addedViews.add(newLessonView);
        addingToTag(v, startingTime, endingTime);

        newLessonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create dialog with all of the request
                if (!moved) {
                    //entering to update mode
                    final RelativeLayout.LayoutParams lParam = (RelativeLayout.LayoutParams) newLessonView.getLayoutParams();
                    if (!newLessonView.getText().toString().contains(",")) {
                        //show requests or updateAll and delete message
                        AlertDialog alertDialog = new AlertDialog.Builder(TeacherSchedulerActivity.this).create();
                        alertDialog.setTitle(getString(R.string.edit_lesson));
                        alertDialog.setMessage(getString(R.string.show_requests_or_update_or_dalete));

                        //update the time for all of the requests
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.update), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removeFromTag(findViewById(getRowIdFromMargin(lParam.topMargin)), newLessonView.getText().toString().split("-")[0], newLessonView.getText().toString().split("-")[1]);
                                newLessonView.setText(newLessonView.getText() + ", press again for saving");
                                newLessonView.setTag("update");
                                prev = newLessonView;
                                prevStarting = newLessonView.getText().toString().split("-")[0];
                                prevMargin = lParam.topMargin;
                                prevColor = TeacherSchedulerActivity.this.getResources().getColor(R.color.lessonHaveRequests);
                            }
                        });
                        //delete all of the requests
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.delete), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //delete from database
                                for(int i=0; i<lessonsList.size();i++){
                                    updateLessonStatusInDB(lessonsList.get(i), Lesson.Status.T_CANCELED);
                                }
                                removeFromTag(findViewById(getRowIdFromMargin(lParam.topMargin)), newLessonView.getText().toString().split("-")[0], newLessonView.getText().toString().split("-")[1]);
                                mLayout.removeView(newLessonView);
                                addedViews.remove(newLessonView);
                                Toast.makeText(TeacherSchedulerActivity.this, R.string.lesson_deleted, Toast.LENGTH_SHORT).show();
                            }
                        });
                        //show all the requests
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.show_requests), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                updatedLessons = new LinkedList<>();
                                updatedLessons.addAll(lessonsList);
                                studentsRequests = new Dialog(TeacherSchedulerActivity.this);
                                studentsRequests.setContentView(R.layout.teacher_choose_student_schedule_lesson_dialog);
                                dateDialog = (TextView) studentsRequests.findViewById(R.id.date_dialog);
                                hourDialog = (TextView) studentsRequests.findViewById(R.id.hour_dialog);
                                finishButton = (Button) studentsRequests.findViewById(R.id.finish_button);

                                finishButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        closingStudentsRequestsDialog(updatedLessons);
                                    }
                                });

                                String dateMessage = dateDialog.getText().toString() + " " + date;
                                String hourMessage = hourDialog.getText().toString() + " " + newLessonView.getText().toString().split("-")[0];
                                dateDialog.setText(dateMessage);
                                hourDialog.setText(hourMessage);
                                initRequestsRecyclerView();
                                studentsRequests.show();
                                studentsRequests.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                                                      @Override
                                                                      public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                                                          if (i == KeyEvent.KEYCODE_BACK) {}
                                                                          return false;
                                                                      }
                                                                  });

                                        requestsAdapter = new TeacherChooseStudentLessonAdapter(TeacherSchedulerActivity.this, lessonsList);
                                ((TeacherChooseStudentLessonAdapter) requestsAdapter)
                                        .setOnItemClickListener(TeacherSchedulerActivity.this);
                                requestsRecyclerView.setAdapter(requestsAdapter);
                            }
                        });
                        alertDialog.show();
                    } else {
                        String time = newLessonView.getText().toString().split(", ")[0];
                        if (newLessonView.getTag().toString().equals("update")) {
                            for(Lesson l:lessonsList){
                                updateLessonInDB(prevStarting, time.split("-")[0],l.getStudentUID());
                                l.setHour(time.split("-")[0]);
                                updateLessonStatusInDB(l, Lesson.Status.T_UPDATE);
                                l.setConformationStatus(Lesson.Status.T_UPDATE);
                            }

                            mLayout.removeView(newLessonView);
                            addedViews.remove(newLessonView);
                            CreateRequestsView(findViewById(getRowIdFromMargin(lParam.topMargin)), time.split("-")[0], lessonsList);
                        }
                    }
                }else{
                    moved = false;
                }
            }
        });

        newLessonView.setOnTouchListener(new View.OnTouchListener() {
            //save textview old parameters
            String time;
            String starting;
            String ending;
            final RelativeLayout.LayoutParams lParam = (RelativeLayout.LayoutParams) newLessonView.getLayoutParams();
            int id;
            TextView prevHour;
            //parameters for updating
            int addition;
            String newStartingHour="", newEndingHour="";

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!newLessonView.getText().toString().contains(",")){
                    return false;
                }
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time = newLessonView.getText().toString().split(",")[0];
                        starting = time.split("-")[0];
                        ending = time.split("-")[1];
                        id = getRowIdFromMargin(lParam.topMargin);
                        prevHour = findViewById(id);

                        //parameters for updating
                        addition = 0;
                        yStart = motionEvent.getY();
                        mScroll.requestDisallowInterceptTouchEvent(true);
                        marginStart = lParam.topMargin;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moved = true;
                        int prevMargin =  lParam.topMargin;
                        yMove = motionEvent.getY();
                        if(lParam.topMargin+((yMove - yStart) / dpi)>=0 && lParam.topMargin+((yMove - yStart) / dpi)<=1100*dpi) {
                            lParam.topMargin += ((yMove - yStart) / dpi);
                            newLessonView.setLayoutParams(lParam);
                        }
                        if (Math.abs((lParam.topMargin - prevMargin) / dpi) >= 5) {
                            addition = ((int) Math.floor(((lParam.topMargin - marginStart) / dpi)) / 5) * 5;
                            newStartingHour = calculateTime(starting, addition);
                            newEndingHour = calculateTime(ending, addition);
                            newLessonView.setText(String.format("%s%s%s", newStartingHour, "-", newEndingHour + ", " + "press again for saving"));
                        }else{
                            lParam.topMargin = prevMargin;
                        }
                        newLessonView.setLayoutParams(lParam);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(!starting.equals(newStartingHour) && !newStartingHour.equals("")) {
                            //removeFromTag(prevHour, starting, ending);
                            lParam.topMargin = (int) (marginStart + addition * dpi);
                            id = getRowIdFromMargin(lParam.topMargin);
                            TextView current = findViewById(id);
                            //check if the lesson can move two the new location,
                            //if the new location is in hour 23:00 so denied (cant start lesson in the last hour
                            if (checkIfCanMove(current, newStartingHour, newEndingHour) && id != R.id.time_23) {
                                //addingToTag(current, newStartingHour, newEndingHour);
                                newLessonView.setLayoutParams(lParam);
                            } else {
                                Toast.makeText(TeacherSchedulerActivity.this, "Cant move lesson to this hour", Toast.LENGTH_SHORT).show();
                                addingToTag(prevHour, starting, ending);
                                newLessonView.setText(time + ", press again for saving");
                                lParam.topMargin = marginStart;
                                newLessonView.setLayoutParams(lParam);
                            }
                        }
                        mScroll.requestDisallowInterceptTouchEvent(false);

                }
                return false;
            }
        });
    }

    private int getRowIdFromMargin(int margin){
        margin = (int)(margin/dpi);
        if(margin>=0 && margin<60){
            return R.id.time_5;
        }
        if(margin>=60 && margin<120){
            return R.id.time_6;
        }
        if(margin>=120 && margin<180){
            return R.id.time_7;
        }
        if(margin>=180 && margin<240){
            return R.id.time_8;
        }
        if(margin>=240 && margin<300){
            return R.id.time_9;
        }
        if(margin>=300 && margin<360){
            return R.id.time_10;
        }
        if(margin>=360 && margin<420){
            return R.id.time_11;
        }
        if(margin>=420 && margin<480){
            return R.id.time_12;
        }
        if(margin>=480 && margin<540){
            return R.id.time_13;
        }
        if(margin>=540 && margin<600){
            return R.id.time_14;
        }
        if(margin>=600 && margin<660){
            return R.id.time_15;
        }
        if(margin>=660 && margin<720){
            return R.id.time_16;
        }
        if(margin>=720 && margin<780){
            return R.id.time_17;
        }
        if(margin>=780 && margin<840){
            return R.id.time_18;
        }
        if(margin>=840 && margin<900){
            return R.id.time_19;
        }
        if(margin>=900 && margin<960){
            return R.id.time_20;
        }
        if(margin>=960 && margin<1020){
            return R.id.time_21;
        }
        if(margin>=1020 && margin<1080){
            return R.id.time_22;
        }
        return R.id.time_23;
    }
    private void resetTags(){
        findViewById(R.id.time_5).setTag("5:00-6:00");
        findViewById(R.id.time_6).setTag("6:00-7:00");
        findViewById(R.id.time_7).setTag("7:00-8:00");
        findViewById(R.id.time_8).setTag("8:00-9:00");
        findViewById(R.id.time_9).setTag("9:00-10:00");
        findViewById(R.id.time_10).setTag("10:00-11:00");
        findViewById(R.id.time_11).setTag("11:00-12:00");
        findViewById(R.id.time_12).setTag("12:00-13:00");
        findViewById(R.id.time_13).setTag("13:00-14:00");
        findViewById(R.id.time_14).setTag("14:00-15:00");
        findViewById(R.id.time_15).setTag("15:00-16:00");
        findViewById(R.id.time_16).setTag("16:00-17:00");
        findViewById(R.id.time_17).setTag("17:00-18:00");
        findViewById(R.id.time_18).setTag("18:00-19:00");
        findViewById(R.id.time_19).setTag("19:00-20:00");
        findViewById(R.id.time_20).setTag("20:00-21:00");
        findViewById(R.id.time_21).setTag("21:00-22:00");
        findViewById(R.id.time_22).setTag("2200-23:00");
        findViewById(R.id.time_23).setTag("23:00-24:00");
    }

    private void initRequestsRecyclerView() {
        requestsRecyclerView = (RecyclerView) studentsRequests.findViewById(R.id.hours_list_requests_dialog);
        requestsRecyclerView.setHasFixedSize(true);
        requestsLayoutManager = new LinearLayoutManager(this);
        requestsRecyclerView.setLayoutManager(requestsLayoutManager);
        requestsRecyclerView.addItemDecoration(new BorderLineDividerItemDecoration(this));
    }

    @Override
    public void onItemClickRequestsList(List<Lesson>lessonsList, final int position) {
        updateLesson = lessonsList.get(position);
        updatedLessons.remove(updateLesson);
        AlertDialog alertDialog = new AlertDialog.Builder(TeacherSchedulerActivity.this).create();
        alertDialog.setTitle(getString(R.string.alert_dialog_request_lesson_title));
        alertDialog.setMessage(getString(R.string.alert_dialog_request_lesson_message));
        if(updateLesson.getConformationStatus() != Lesson.Status.T_CONFIRMED) {
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.confirm_lesson), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (oneRequestApprove) {
                        Toast.makeText(getApplicationContext(), getString(R.string.cant_approve_more_than_one_request), Toast.LENGTH_SHORT).show();
                    }else{
                        if(updateLesson.getConformationStatus() == Lesson.Status.T_UPDATE){
                            Toast.makeText(getApplicationContext(),getString(R.string.wait_for_student_response), Toast.LENGTH_SHORT).show();
                        } else {
                            updateStatus = Lesson.Status.T_CONFIRMED;
                            updatedLessons.add(updateLesson);
                            oneRequestApprove = true;
                            requestsRecyclerView.findViewHolderForLayoutPosition(position).itemView.setBackgroundColor(Color.GREEN);
                            updateLessonStatusInDB(updateLesson, updateStatus);
                            Toast.makeText(getApplicationContext(), getString(R.string.lesson_confirmed), Toast.LENGTH_SHORT).show();
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
                    updatedLessons.add(updateLesson);
                    requestsRecyclerView.findViewHolderForLayoutPosition(position).itemView.setBackgroundColor(Color.RED);
                    updateLessonStatusInDB(updateLesson, updateStatus);
                    Toast.makeText(getApplicationContext(), getString(R.string.lesson_reject), Toast.LENGTH_SHORT).show();
                }
            });
        }
        alertDialog.show();
    }

    private void closingStudentsRequestsDialog(List<Lesson> lessonsList){
        if(oneRequestApprove){
            for(Lesson l: lessonsList){
                if(l.getConformationStatus() == Lesson.Status.S_REQUEST || l.getConformationStatus() == Lesson.Status.S_UPDATE ||
                        l.getConformationStatus() == Lesson.Status.S_CONFIRMED || l.getConformationStatus() == Lesson.Status.T_UPDATE ){
                    Toast.makeText(TeacherSchedulerActivity.this,getString(R.string.need_to_delete_or_update), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        updateDayView();
        studentsRequests.hide();
    }

    @Override
    public void onEditButtonClickRequestsList(List<Lesson> lessonsList, final int position) {
        updateLesson = lessonsList.get(position);
        updatedLessons.remove(updateLesson);
        updateRequests = new Dialog(TeacherSchedulerActivity.this);
        updateRequests.setContentView(R.layout.teacher_update_lesson_request);

        studentNameUpdateDialog = (TextView) updateRequests.findViewById(R.id.student_name);
        dateUpdateDialog = (TextView) updateRequests.findViewById(R.id.lesson_date);
        startingLocationUpdateDialog = (TextView) updateRequests.findViewById(R.id.setStartingLocation);
        endingLocationUpdateDialog = (TextView) updateRequests.findViewById(R.id.setEndingLocation);
        update = (Button) updateRequests.findViewById(R.id.update_lesson);
        datePicker = (Button) updateRequests.findViewById(R.id.date_picker);

        spinner = updateRequests.findViewById(R.id.hoursSpinner);
        if (freeLessons != null) {
            freeLessonsSorted = new String[freeLessons.size() + 1];
            freeLessonsSorted[0] = updateLesson.getHour();
            lessonsTemp = new LinkedList<>();
            lessonsTemp.addAll(freeLessons);
            //order by hours
            for (int i = 0; i < freeLessons.size(); i++) {
                String min = lessonsTemp.get(0);
                for (int j = 0; j < lessonsTemp.size(); j++) {
                    if (hourLowerThan(lessonsTemp.get(j), min)) {
                        min = lessonsTemp.get(j);
                    }
                }
                lessonsTemp.remove(min);
                freeLessonsSorted[i + 1] = min;
            }
        } else {
            freeLessonsSorted = new String[1];
            freeLessonsSorted[0] = updateLesson.getHour();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, freeLessonsSorted);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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
        startingLocationUpdateDialog.setText(updateLesson.getStartingLocation());
        endingLocationUpdateDialog.setText(updateLesson.getEndingLocation());
        updateRequests.show();
        //edit date for the lesson
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        TeacherSchedulerActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        updateRequestsDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        updateRequestsDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                updateDate = day + "/" + month + "/" + year;
                dateUpdateDialog.setText(updateDate);

                db.collection("lessons").whereEqualTo("teacherUID", mTeacher.getID()).whereEqualTo("date", updateDate)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            freeLessonsInDate = new LinkedList<>();
                            List<Lesson> toRemove = new LinkedList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Lesson lesson = document.toObject(Lesson.class);
                                if (lesson.getConformationStatus() == Lesson.Status.S_CANCELED || lesson.getConformationStatus() == Lesson.Status.T_CANCELED) {
                                    toRemove.add(lesson);
                                }
                                if (lesson.getConformationStatus() == Lesson.Status.T_OPTION) {
                                    freeLessonsInDate.add(lesson.getHour());
                                }
                            }
                            for (Lesson l : toRemove) {
                                if (freeLessonsInDate.contains(l.getHour())) {
                                    freeLessonsInDate.remove(l.getHour());
                                }
                            }

                            if (updateDate.equals(date)) {
                                freeLessonsSortedInDate = freeLessonsSorted;
                            } else {
                                if (freeLessonsInDate != null) {
                                    freeLessonsSortedInDate = new String[freeLessonsInDate.size()];
                                    lessonsTemp = new LinkedList<>();
                                    lessonsTemp.addAll(freeLessonsInDate);
                                    //order by hours
                                    for (int i = 0; i < freeLessonsInDate.size(); i++) {
                                        String min = lessonsTemp.get(0);
                                        for (int j = 0; j < lessonsTemp.size(); j++) {
                                            if (hourLowerThan(lessonsTemp.get(j), min)) {
                                                min = lessonsTemp.get(j);
                                            }
                                        }
                                        lessonsTemp.remove(min);
                                        freeLessonsSortedInDate[i] = min;
                                    }
                                } else {
                                    freeLessonsSortedInDate = new String[0];
                                }
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(TeacherSchedulerActivity.this,
                                    android.R.layout.simple_spinner_item, freeLessonsSortedInDate);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);

                        }
                    }
                });
            }
        };

        update.setOnClickListener(new View.OnClickListener() {
            String newDate;
            String newHour;

            @Override
            public void onClick(View view) {
                if (dateUpdateDialog.getText() == "" || spinner.getSelectedItem() == null) {
                    Toast.makeText(TeacherSchedulerActivity.this, getString(R.string.select_all_values), Toast.LENGTH_SHORT).show();
                } else {
                    if (spinner.getSelectedItemPosition() == 0 && dateUpdateDialog.getText().toString().equals(date)) {
                        Toast.makeText(TeacherSchedulerActivity.this, "Nothing to update", Toast.LENGTH_SHORT).show();
                    } else {
                        newHour = spinner.getSelectedItem().toString();
                        newDate = dateUpdateDialog.getText().toString();
                        db.collection("lessons").document(updateLesson.getLessonID()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                updateLesson.setHour(newHour);
                                updateLesson.setDate(newDate);
                                updateLesson.setConformationStatus(Lesson.Status.T_UPDATE);
                                db.collection("lessons").whereEqualTo("date", newDate).whereEqualTo("hour", newHour)
                                        .whereEqualTo("teacherUID", mTeacher.getID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            String id = "";
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                id = document.getId();
                                            }
                                            db.collection("lessons").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    db.collection("lessons").add(updateLesson);

                                                    updatedLessons.add(updateLesson);
                                                    requestsRecyclerView.findViewHolderForLayoutPosition(position).itemView.setBackgroundColor(Color.YELLOW);
                                                    updateRequests.hide();
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        });
                    }
                }
            }
        });

    }
    private boolean hourLowerThan(String hour1, String hour2){
        //return true if hour1 is before hour2
        int time1 = Integer.valueOf(hour1.split(":")[0]) * 60 + Integer.valueOf(hour1.split(":")[1]);
        int time2 = Integer.valueOf(hour2.split(":")[0]) * 60 + Integer.valueOf(hour2.split(":")[1]);

        if(time1<=time2){
            return true;
        }
        return false;
    }
}