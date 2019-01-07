package com.example.eliad.drive4u;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eliad.drive4u.models.Teacher;


public class TeacherProfileFragment extends TeacherBaseFragment {

    private static final String TAG = TeacherProfileFragment.class.getName();

    // widgets
    private TextView textViewFirstName, textViewLastName, textViewPhoneNumber, textViewEmail;
    private TextView textViewCity, textViewBalance, textViewCarModel, textViewGearType;
    private TextView textViewPrice, textViewTotalPayed;

    public static TeacherProfileFragment newInstance(Teacher t) {
        TeacherProfileFragment fragment = new TeacherProfileFragment();
        Bundle args = getBaseBundle(t);
        fragment.setArguments(args);
        return fragment;
    }

    public TeacherProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTeacher = getArguments().getParcelable(ARG_TEACHER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_teacher_profile, container, false);

        if (mTeacher == null && getArguments() == null) {
            Log.d(TAG, "Got a Null teacher and no arguments");
            return rootView;
        }

        if (mTeacher == null) {
            mTeacher = getArguments().getParcelable(ARG_TEACHER);
        }

        String text;
        // init text views
        textViewFirstName       = rootView.findViewById(R.id.TeacherProfileFirstName);
        textViewLastName        = rootView.findViewById(R.id.TeacherProfileLastName);
        textViewPhoneNumber     = rootView.findViewById(R.id.TeacherProfilePhone);
        textViewEmail           = rootView.findViewById(R.id.TeacherProfileEmail);
        textViewCity            = rootView.findViewById(R.id.TeacherProfileCity);
        textViewBalance         = rootView.findViewById(R.id.TeacherProfileBalance);
        textViewCarModel        = rootView.findViewById(R.id.TeacherProfileCarModel);
        textViewGearType        = rootView.findViewById(R.id.TeacherProfileGearType);
        textViewPrice           = rootView.findViewById(R.id.TeacherProfilePrice);
        textViewTotalPayed      = rootView.findViewById(R.id.TeacherProfileTotalPayed);

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

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
