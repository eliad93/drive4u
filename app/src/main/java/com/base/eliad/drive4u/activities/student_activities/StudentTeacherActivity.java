package com.base.eliad.drive4u.activities.student_activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.base_activities.StudentBaseActivity;
import com.base.eliad.drive4u.models.Teacher;
import com.base.eliad.drive4u.models.User;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentTeacherActivity extends StudentBaseActivity {

    private static final String TAG = StudentTeacherActivity.class.getName();

    CircleImageView image_profile;
    TextView username;

    private TextView textViewPhoneNumber, textViewEmail, textViewCity, textViewCarModel,
            textViewGearType, textViewPrice;

    private Teacher mTeacher;

    public static final String ARG_TEACHER = Teacher.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_teacher);

        mTeacher = getIntent().getParcelableExtra(ARG_TEACHER);

        initViews();

        updateProfile();

    }

    private void initViews() {
        image_profile = findViewById(R.id.profile_image);
        username = findViewById(R.id.profile_username);
        textViewPhoneNumber = findViewById(R.id.StudentTeacherProfilePhone);
        textViewEmail = findViewById(R.id.StudentTeacherProfileEmail);
        textViewCity = findViewById(R.id.StudentTeacherProfileCity);
        textViewCarModel = findViewById(R.id.StudentTeacherProfileCarModel);
        textViewGearType = findViewById(R.id.StudentTeacherProfileGearType);
        textViewPrice = findViewById(R.id.StudentTeacherProfilePrice);
        ImageView imageView = findViewById(R.id.imageViewEditPhoto);
        imageView.setVisibility(View.GONE);
    }

    private void updateProfile() {
        username.setText(mTeacher.getFullName());
        textViewPhoneNumber.setText(mTeacher.getPhoneNumber());
        textViewEmail.setText(mTeacher.getEmail());
        textViewCity.setText(mTeacher.getCity());
        textViewCarModel.setText(mTeacher.getCarModel());
        textViewGearType.setText(mTeacher.getGearType());
        String text = Integer.toString(mTeacher.getPrice());
        textViewPrice.setText(text);

        if (mTeacher.getImageUrl() == null || mTeacher.getImageUrl().equals(User.DEFAULT_IMAGE_KEY)) {
            image_profile.setImageResource(R.mipmap.ic_user_foreground );
        } else {
            Glide.with(this).load(mTeacher.getImageUrl()).into(image_profile);
        }
    }
}
