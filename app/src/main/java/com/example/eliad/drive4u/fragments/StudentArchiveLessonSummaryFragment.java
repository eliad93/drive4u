package com.example.eliad.drive4u.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Lesson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentArchiveLessonSummaryFragment.StudentLessonSummaryFragmentListener} interface
 * to handle interaction events.
 * Use the {@link StudentArchiveLessonSummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentArchiveLessonSummaryFragment extends Fragment {
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    // Tag for the Log
    private static final String TAG = StudentArchiveLessonSummaryFragment.class.getName();
    // Firebase
    private DocumentReference lessonDoc;
    // widgets
    private Button buttonClose;
    // models
    private Lesson lesson;

    private StudentLessonSummaryFragmentListener mListener;

    public StudentArchiveLessonSummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param lesson Parameter 1.
     * @return A new instance of fragment StudentArchiveLessonSummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentArchiveLessonSummaryFragment newInstance(Lesson lesson) {
        StudentArchiveLessonSummaryFragment fragment = new StudentArchiveLessonSummaryFragment();
        fragment.lesson = lesson;
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_lesson_arcive_summary,
                container, false);
        buttonClose = (Button) view.findViewById(R.id.buttonArchiveLessonClose);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed(); // TODO: how can we avoid it?
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
        if (context instanceof StudentLessonSummaryFragmentListener) {
            mListener = (StudentLessonSummaryFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface StudentLessonSummaryFragmentListener {
        void onFragmentInteraction(Lesson lesson);
    }
}
