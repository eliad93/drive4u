package com.base.eliad.drive4u.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.models.Lesson;
import com.google.firebase.firestore.DocumentReference;

public class StudentArchiveLessonSummaryFragment extends Fragment {

    // Tag for the Log
    private static final String TAG = StudentArchiveLessonSummaryFragment.class.getName();
    // Firebase
    private DocumentReference lessonDoc;
    // widgets
    private Button buttonClose;

    private StudentLessonSummaryFragmentListener mListener;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"in onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_lesson_arcive_summary,
                container, false);
        buttonClose = view.findViewById(R.id.buttonArchiveLessonClose);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if(activity != null){
                    getActivity().onBackPressed(); // TODO: how can we avoid it?
                }
            }
        });
        return view;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onSubmit(View view) {
//        String paragraph = ""; // TODO: add edit texts and retrieve data
//        int grade = 0;
//        lesson.summarize(paragraph, grade);
//        lessonDoc.update("summary", lesson.getSummary())
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//                            if (mListener != null) {
//                                mListener.onFragmentInteraction(lesson);
//                            }
//                        } else {
//                            Log.d(TAG, "lesson summary update failed");
//                        }
//                    }
//                });
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG,"in onAttach");
        if (context instanceof StudentLessonSummaryFragmentListener) {
            mListener = (StudentLessonSummaryFragmentListener) context;
        } else {
            Log.e(TAG,"calling activity/fragment does not implement " +
                    "StudentLessonSummaryFragmentListener");
            Activity activity = getActivity();
            if(activity != null){
                getActivity().onBackPressed();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface StudentLessonSummaryFragmentListener {
        void onFragmentInteraction(Lesson lesson);
    }
}
