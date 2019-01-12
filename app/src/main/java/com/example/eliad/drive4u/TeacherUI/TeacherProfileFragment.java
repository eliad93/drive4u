package com.example.eliad.drive4u.TeacherUI;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Teacher;

public class TeacherProfileFragment extends TeacherBaseFragment {
    private static final String TAG = TeacherProfileFragment.class.getName();

    public TeacherProfileFragment() {
        // Required empty public constructor
    }

    public static TeacherProfileFragment newInstance(Teacher teacher) {
        TeacherProfileFragment fragment = new TeacherProfileFragment();
        Bundle args = newInstanceBaseArgs(teacher);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_profile, container, false);

        String text;
        // init text views
        TextView textViewFirstName = view.findViewById(R.id.TeacherProfileFirstName);
        TextView textViewLastName = view.findViewById(R.id.TeacherProfileLastName);
        TextView textViewPhoneNumber = view.findViewById(R.id.TeacherProfilePhone);
        TextView textViewEmail = view.findViewById(R.id.TeacherProfileEmail);
        TextView textViewCity = view.findViewById(R.id.TeacherProfileCity);
        TextView textViewBalance = view.findViewById(R.id.TeacherProfileBalance);
        TextView textViewCarModel = view.findViewById(R.id.TeacherProfileCarModel);
        TextView textViewGearType = view.findViewById(R.id.TeacherProfileGearType);
        TextView textViewPrice = view.findViewById(R.id.TeacherProfilePrice);
        TextView textViewTotalPayed = view.findViewById(R.id.TeacherProfileTotalPayed);

        textViewFirstName.setText(mTeacher.getFirstName());
        textViewLastName.setText(mTeacher.getLastName());
        textViewPhoneNumber.setText(mTeacher.getPhoneNumber());
        textViewEmail.setText(mTeacher.getEmail());
        textViewCity.setText(mTeacher.getCity());
        text = Integer.toString(mTeacher.getBalance());
        textViewBalance.setText(text);
        textViewCarModel.setText(mTeacher.getCarModel());
        textViewGearType.setText(mTeacher.getGearType());
        text = Integer.toString(mTeacher.getPrice());
        textViewPrice.setText(text);
        text = Integer.toString(mTeacher.getTotalPayed());
        textViewTotalPayed.setText(text);

        return view;
    }

}
