package com.example.eliad.drive4u;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.eliad.drive4u.base_activities.StudentBaseActivity;
import com.example.eliad.drive4u.models.Student;

public class StudentProfileActivity extends StudentBaseActivity {
    // tag for log
    private static final String TAG = StudentProfileActivity.class.getName();

    private TextView textViewFirstName, textViewLastName, textViewPhoneNumber, textViewEmail;
    private TextView textViewCity, textViewBalance, textViewTotaleExpence, textViewNumberOfLessons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        Log.d(TAG, "in onCreate");

        String text;
        // init text views
        textViewFirstName       = findViewById(R.id.StudentProfileFirstName);
        textViewLastName        = findViewById(R.id.StudentProfileLastName);
        textViewPhoneNumber     = findViewById(R.id.StudentProfilePhone);
        textViewEmail           = findViewById(R.id.StudentProfileEmail);
        textViewCity            = findViewById(R.id.StudentProfileCity);
        textViewBalance         = findViewById(R.id.StudentProfileBalance);
        textViewTotaleExpence   = findViewById(R.id.StudentProfileTotalExpense);
        textViewNumberOfLessons = findViewById(R.id.StudentProfileNumberOfLessons);

        // set the content
        textViewFirstName.setText(mStudent.getFirstName());
        textViewLastName.setText(mStudent.getLastName());
        textViewPhoneNumber.setText(mStudent.getPhoneNumber());
        textViewEmail.setText(mStudent.getEmail());
        textViewCity.setText(mStudent.getCity());
        text = Integer.toString(mStudent.getBalance());
        textViewBalance.setText(text);
        text = Integer.toString(mStudent.getTotalExpense());
        textViewTotaleExpence.setText(text);
        text = Integer.toString(mStudent.getNumberOfLessons());
        textViewNumberOfLessons.setText(text);

    }
}
