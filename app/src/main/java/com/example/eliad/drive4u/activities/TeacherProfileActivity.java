package com.example.eliad.drive4u.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.base_activities.TeacherBaseActivity;

public class TeacherProfileActivity extends TeacherBaseActivity {
    // for log and debugging
    private static final String TAG = TeacherProfileActivity.class.getName();

    // widgets
    private TextView textViewFirstName, textViewLastName, textViewPhoneNumber, textViewEmail;
    private TextView textViewCity, textViewBalance, textViewCarModel, textViewGearType;
    private TextView textViewPrice, textViewTotalPayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        String text;
        // init text views
        textViewFirstName       = findViewById(R.id.TeacherProfileFirstName);
        textViewLastName        = findViewById(R.id.TeacherProfileLastName);
        textViewPhoneNumber     = findViewById(R.id.TeacherProfilePhone);
        textViewEmail           = findViewById(R.id.TeacherProfileEmail);
        textViewCity            = findViewById(R.id.TeacherProfileCity);
        textViewBalance         = findViewById(R.id.TeacherProfileBalance);
        textViewCarModel        = findViewById(R.id.TeacherProfileCarModel);
        textViewGearType        = findViewById(R.id.TeacherProfileGearType);
        textViewPrice           = findViewById(R.id.TeacherProfilePrice);
        textViewTotalPayed      = findViewById(R.id.TeacherProfileTotalPayed);

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
        text = Integer.toString(mTeacher.getTotalPaid());
        textViewTotalPayed.setText(text);
    }
}
