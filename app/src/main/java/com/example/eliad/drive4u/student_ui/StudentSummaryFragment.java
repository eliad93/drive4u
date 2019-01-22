package com.example.eliad.drive4u.student_ui;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.StudentHomeLessonsAdapter;
import com.example.eliad.drive4u.built_in_utils.BorderLineDividerItemDecoration;
import com.example.eliad.drive4u.fragments.TimePickerFragment;
import com.example.eliad.drive4u.models.Lesson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.Contract;

import java.util.Calendar;
import java.util.LinkedList;

public class StudentSummaryFragment extends StudentBaseFragment implements
        StudentHomeLessonsAdapter.OnItemClickListener,
        TimePickerDialog.OnTimeSetListener{
    private static final String TAG = StudentSummaryFragment.class.getName();

    // widgets and recycler view items
    private TextView textViewLessonsCompleted;
    private TextView textViewBalance;
    private TextView textViewNoLessons;

    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //updated lesson Status
    private Lesson.Status updateStatus;
    private Lesson updateLesson;


    //teacher update student request for lesson dialog
    private Dialog updateLessonDialog;
    private TextView dateUpdateDialog;
    private TextView hourUpdateDialog;
    private TextView startingLocationUpdateDialog;
    private TextView endingLocationUpdateDialog;
    private Button update;
    private Button timePicker;
    private Button datePicker;
    private DatePickerDialog.OnDateSetListener updateRequestsDateSetListener;
    private String date;
    LinkedList<Lesson> lessons = new LinkedList<>();

    private View view;

    public StudentSummaryFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Contract(" -> new")
    public static StudentSummaryFragment newInstance() {
        return new StudentSummaryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_student_summary, container, false);

        // init widgets
        textViewBalance = view.findViewById(R.id.textViewBalance);
        textViewLessonsCompleted = view.findViewById(R.id.textViewLessonsCompleted);
        textViewNoLessons = view.findViewById(R.id.textViewNoLessons);

        String text = Integer.toString(mStudent.getBalance());
        textViewBalance.setText(text);
        text = Integer.toString(mStudent.getNumberOfLessons());  // TODO: fix to the correct number
        textViewLessonsCompleted.setText(text);

        initLessonsRecyclerView();

        updateLessonsView();

        return view;
    }

    private void updateLessonsView() {
        Log.d(TAG, "in presentNextLessons");
        lessons = new LinkedList<>();
        db.collection(getString(R.string.DB_Lessons))
                .whereEqualTo("studentUID", mStudent.getID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "received document " + document.getId());
                                Lesson lesson = document.toObject(Lesson.class);
                                if (lesson.getConformationStatus() != Lesson.Status.S_CANCELED) {
                                    lessons.addLast(lesson);
                                }
                            }
                            if (lessons.size() > 0) {
                                mAdapter = new StudentHomeLessonsAdapter(getContext(), lessons);
                                ((StudentHomeLessonsAdapter) mAdapter)
                                        .setOnItemClickListener(StudentSummaryFragment.this);
                                mRecyclerView.setAdapter(mAdapter);
                            } else {
                                Log.d(TAG, "Student " + mStudent.getEmail() + " has no lessons to present");
                                textViewNoLessons.setVisibility(View.VISIBLE);
                                textViewNoLessons.setText(R.string.you_have_no_lessons);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void initLessonsRecyclerView() {
        Log.d(TAG, "in initLessonsRecyclerView");
        mRecyclerView = view.findViewById(R.id.recyclerViewNextLessons);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new BorderLineDividerItemDecoration(getContext()));
    }

    @Override
    public void onItemClick(int position) {
        updateLesson = lessons.get(position);
        if(updateLesson.getConformationStatus() == Lesson.Status.T_CANCELED){
            deleteLesson(updateLesson);
        }else{
            confirmedOrRejectLesson(updateLesson);
        }
    }

    @Override
    public void onEditButtonClick(final int position) {
        updateLesson = lessons.get(position);
        updateLessonDialog = new Dialog(getContext());
        updateLessonDialog.setContentView(R.layout.student_update_lesson_request);

        dateUpdateDialog = (TextView) updateLessonDialog.findViewById(R.id.lesson_date);
        hourUpdateDialog = (TextView) updateLessonDialog.findViewById(R.id.starting_time);
        startingLocationUpdateDialog = (TextView) updateLessonDialog.findViewById(R.id.setStartingLocation);
        endingLocationUpdateDialog = (TextView) updateLessonDialog.findViewById(R.id.setEndingLocation);
        update = (Button) updateLessonDialog.findViewById(R.id.update_lesson);
        timePicker = (Button) updateLessonDialog.findViewById(R.id.time_picker);
        datePicker = (Button) updateLessonDialog.findViewById(R.id.date_picker);

        dateUpdateDialog.setText(updateLesson.getDate());
        hourUpdateDialog.setText(updateLesson.getHour());
        startingLocationUpdateDialog.setText(updateLesson.getStartingLocation());
        endingLocationUpdateDialog.setText(updateLesson.getEndingLocation());
        updateLessonDialog.show();


        //edit starting time for the lesson
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getChildFragmentManager(), "time picker");
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
                        getContext(),
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
                date = day + "/" + month + "/" + year;
                dateUpdateDialog.setText(date);
            }
        };


        update.setOnClickListener(new View.OnClickListener() {
            String newDate;
            String newHour;

            @Override
            public void onClick(View view) {
                if (hourUpdateDialog.getText() == "") {
                    Toast.makeText(getContext(), R.string.select_hour, Toast.LENGTH_SHORT).show();
                } else {
                    if (dateUpdateDialog.getText() == "") {
                        Toast.makeText(getContext(), R.string.select_date, Toast.LENGTH_SHORT).show();
                    } else {
                        updateStatus = Lesson.Status.S_UPDATE;
                        newHour = hourUpdateDialog.getText().toString();
                        newDate = dateUpdateDialog.getText().toString();
                        updateLesson.setConformationStatus(updateStatus);
                        updateLesson.setDate(newDate);
                        updateLesson.setHour(newHour);
                        db.collection("lessons").document(updateLesson.getLessonID()).update("date", newDate, "hour", newHour, "conformationStatus", updateStatus.toString());
                        lessons.set(position, updateLesson);
                        updateLessonDialog.hide();
                        updateLessonsView();

                    }
                }
            }
        });

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        if (minute > 0) {
            Toast.makeText(getContext(), R.string.full_hour, Toast.LENGTH_SHORT).show();
        } else {
            if (hour > 20 || hour < 7) {
                Toast.makeText(getContext(), R.string.full_hour, Toast.LENGTH_SHORT).show();
            } else {
                if (hour < 10) {
                    String fullHour = "0" + hour + ":" + "00";
                    hourUpdateDialog.setText(fullHour);
                } else {
                    String fullHour = hour + ":" + "00";
                    hourUpdateDialog.setText(fullHour);
                }
            }
        }
    }

    private void deleteLesson(final Lesson updateLesson){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(getString(R.string.remove_from_home_page));
        alertDialog.setMessage(getString(R.string.remove_from_home_page_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.confirm_lesson), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                db.collection("lessons").document(updateLesson.getLessonID()).delete();
                Toast.makeText(getContext(),getString(R.string.lesson_deleted), Toast.LENGTH_SHORT).show();
                updateLessonsView();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.reject_lesson), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(),getString(R.string.lesson_did_not_deleted), Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }

    private void confirmedOrRejectLesson(final Lesson updateLesson) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(getString(R.string.alert_dialog_request_lesson_title));
        alertDialog.setMessage(getString(R.string.alert_dialog_request_lesson_message));
        if(updateLesson.getConformationStatus() == Lesson.Status.T_UPDATE) {
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.confirm_lesson), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    updateStatus = Lesson.Status.S_CONFIRMED;
                    db.collection("lessons").document(updateLesson.getLessonID()).update("conformationStatus", updateStatus.toString());
                    Toast.makeText(getContext(), "Lesson confirmed!", Toast.LENGTH_SHORT).show();
                    updateLessonsView();
                }
            });
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel_lesson), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                updateStatus = Lesson.Status.S_CANCELED;
                if (updateLesson.getConformationStatus() == Lesson.Status.T_CONFIRMED) {
                    updateLesson.setConformationStatus(updateStatus);
                    db.collection("lessons").document(updateLesson.getLessonID()).update("conformationStatus", updateStatus.toString());
                } else {
                    db.collection("lessons").document(updateLesson.getLessonID()).delete();
                }
                Toast.makeText(getContext(), "Lesson rejected", Toast.LENGTH_SHORT).show();
                updateLessonsView();

            }
        });
        alertDialog.show();
    }
}
