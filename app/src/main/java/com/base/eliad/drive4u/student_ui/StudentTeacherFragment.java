package com.base.eliad.drive4u.student_ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.eliad.drive4u.models.Student;
import com.base.eliad.drive4u.models.Teacher;
import com.base.eliad.drive4u.models.User;
import com.bumptech.glide.Glide;
import com.base.eliad.drive4u.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class StudentTeacherFragment extends StudentBaseFragment {

    private static final String TAG = StudentTeacherFragment.class.getName();

    CircleImageView image_profile;
    TextView username;

    private TextView textViewPhoneNumber, textViewEmail, textViewCity, textViewCarModel,
            textViewGearType, textViewPrice, noTeacher;

    private Teacher mTeacher;

    public static final String ARG_TEACHER = Teacher.class.getName();

    public StudentTeacherFragment() {
        // Required empty public constructor
    }

    public static StudentTeacherFragment newInstance(Teacher teacher) {
        StudentTeacherFragment fragment = new StudentTeacherFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TEACHER, teacher);
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_teacher, container, false);

        initViews(view);

        updateProfile();

        return view;
    }

    private void setNoTeacher() {

    }

    private void initViews(View view) {
        image_profile = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.profile_username);
        textViewPhoneNumber = view.findViewById(R.id.StudentTeacherProfilePhone);
        textViewEmail = view.findViewById(R.id.StudentTeacherProfileEmail);
        textViewCity = view.findViewById(R.id.StudentTeacherProfileCity);
        textViewCarModel = view.findViewById(R.id.StudentTeacherProfileCarModel);
        textViewGearType = view.findViewById(R.id.StudentTeacherProfileGearType);
        textViewPrice = view.findViewById(R.id.StudentTeacherProfilePrice);
        ImageView imageView = view.findViewById(R.id.imageViewEditPhoto);
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
            if (getContext() == null) {
                return;
            }
            Glide.with(getContext()).load(mTeacher.getImageUrl()).into(image_profile);
        }
    }
}
