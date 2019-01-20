package com.example.eliad.drive4u.student_ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Student;

/**
 * A simple {@link StudentBaseFragment} subclass.
 * Use the {@link StudentProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentProfileFragment extends StudentBaseFragment {
    // Tag for the Log
    private static final String TAG = StudentProfileFragment.class.getName();
    // widgets
    private TextView textViewFirstName, textViewLastName, textViewPhoneNumber, textViewEmail;
    private TextView textViewCity, textViewBalance, textViewTotalExpence, textViewNumberOfLessons;

    public StudentProfileFragment() {
        // Required empty public constructor
    }

    public static StudentProfileFragment newInstance(Student student) {
        StudentProfileFragment fragment = new StudentProfileFragment();
        Bundle args = newInstanceBaseArgs(student);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_student_profile,
                container, false);
        String text;
        // init text views
        textViewFirstName       = view.findViewById(R.id.StudentProfileFirstName);
        textViewLastName        = view.findViewById(R.id.StudentProfileLastName);
        textViewPhoneNumber     = view.findViewById(R.id.StudentProfilePhone);
        textViewEmail           = view.findViewById(R.id.StudentProfileEmail);
        textViewCity            = view.findViewById(R.id.StudentProfileCity);
        textViewBalance         = view.findViewById(R.id.StudentProfileBalance);
        textViewTotalExpence = view.findViewById(R.id.StudentProfileTotalExpense);
        textViewNumberOfLessons = view.findViewById(R.id.StudentProfileNumberOfLessons);

        // set the content
        textViewFirstName.setText(mStudent.getFirstName());
        textViewLastName.setText(mStudent.getLastName());
        textViewPhoneNumber.setText(mStudent.getPhoneNumber());
        textViewEmail.setText(mStudent.getEmail());
        textViewCity.setText(mStudent.getCity());
        text = Integer.toString(mStudent.getBalance());
        textViewBalance.setText(text);
        text = Integer.toString(mStudent.getTotalExpense());
        textViewTotalExpence.setText(text);
        text = Integer.toString(mStudent.getNumberOfLessons());
        textViewNumberOfLessons.setText(text);
        return view;
    }

}
