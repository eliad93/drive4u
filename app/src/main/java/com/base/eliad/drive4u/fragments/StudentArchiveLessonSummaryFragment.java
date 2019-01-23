package com.base.eliad.drive4u.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.models.Lesson;
import com.base.eliad.drive4u.models.Teacher;
import com.base.eliad.drive4u.student_ui.StudentBaseFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class StudentArchiveLessonSummaryFragment extends StudentBaseFragment {

    // Tag for the Log
    private static final String TAG = StudentArchiveLessonSummaryFragment.class.getName();
    public static final String ARG_LESSON = TAG + "lesson";
    // Firebase
    private Lesson lesson;
    // widgets
    private TextView textViewDate;
    private TextView textViewTeacher;
    private TextView textViewStartLocation;
    private TextView textViewEndLocation;
    private TextView textViewGrade;
    private TextView textViewSummary;

    public StudentArchiveLessonSummaryFragment() {
        // Required empty public constructor
    }

    public static StudentArchiveLessonSummaryFragment newInstance() {
        Log.d(TAG,"in StudentArchiveLessonSummaryFragment");
        return new StudentArchiveLessonSummaryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"in onCreate");
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"in onCreateView");
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_student_lesson_arcive_summary,
                container, false);
        lesson = getArguments().getParcelable(ARG_LESSON);
        if(lesson == null){
            unexpectedError();
            getActivity().onBackPressed();
        }
        initWidgets(view);
        return view;
    }

    private void initWidgets(View view) {
        textViewDate = view.findViewById(R.id.textViewArchiveLessonDate);
        textViewTeacher = view.findViewById(R.id.textViewArchiveLessonTeacher);
        textViewStartLocation = view.findViewById(R.id.textViewArchiveLessonLocationStart);
        textViewEndLocation = view.findViewById(R.id.textViewArchiveLessonLocationEnd);
        textViewGrade = view.findViewById(R.id.textViewArchiveLessonGrade);
        textViewSummary = view.findViewById(R.id.textViewArchiveLessonSummary);

        textViewDate.setText(lesson.getDate());
        getTeacherDoc(lesson.getTeacherUID()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Teacher teacher = documentSnapshot.toObject(Teacher.class);
                        if(teacher != null){
                            if(teacher.getFullName() != null)
                                textViewTeacher.setText(teacher.getFullName());
                        }
                    }
                });
        if(lesson.getStartingLocation() != null && !lesson.getStartingLocation().isEmpty()){
            textViewStartLocation.setText(lesson.getStartingLocation());
        }
        if(lesson.getEndingLocation() != null && !lesson.getEndingLocation().isEmpty()){
            textViewEndLocation.setText(lesson.getEndingLocation());
        }
        if(lesson.getSummary().getGrade() != null){
            textViewGrade.setText(String.format("Grade: %s", lesson.getSummary().getGrade()));
        }
        if(lesson.getSummary().getParagraph() != null
                && !lesson.getSummary().getParagraph().isEmpty()){
            textViewSummary.setText(lesson.getSummary().getParagraph());
        }
    }

}
