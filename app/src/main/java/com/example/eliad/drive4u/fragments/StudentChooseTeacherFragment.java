package com.example.eliad.drive4u.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.StudentUI.StudentBaseFragment;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link StudentBaseFragment} subclass.
 * Use the {@link StudentChooseTeacherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentChooseTeacherFragment extends StudentBaseFragment
        implements PositiveNegativeDialog.OnClickDialogButton {
    // Tag for the Log
    private static final String TAG = StudentChooseTeacherFragment.class.getName();
    // widgets
    CircleImageView teacherImage;
    TextView textViewTeacherName;
    TextView textViewTeacherCity;
    TextView textViewTeacherNumStudents;
    TextView textViewTeacherGear;
    TextView textViewTeacherCar;
    Button buttonChooseTeacher;
    // fragments
    private DialogFragment dialogFragment;
    // models
    Teacher mTeacher;
    private PerformUserAction callBack;

    public interface PerformUserAction{
        public void performUserAction();
    }

    public StudentChooseTeacherFragment() {
        // Required empty public constructor
    }

    public static StudentChooseTeacherFragment newInstance(Student student, Teacher teacher) {
        Log.d(TAG, "in newInstance");
        StudentChooseTeacherFragment fragment = new StudentChooseTeacherFragment();
        Bundle args = newInstanceBaseArgs(student);
        args.putParcelable(ARG_TEACHER, teacher);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment f = getTargetFragment();
        if(f instanceof PerformUserAction){
            callBack = (PerformUserAction) f;
        } else {
            Log.d(TAG, "activity does not implement PerformUserAction");
        }
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTeacher = arguments.getParcelable(ARG_TEACHER);
        } else {
            Log.d(TAG, "no teacher");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_choose_teacher,
                container, false);
        initWidgets(view);
        initChooseTeacherDialog();
        return view;
    }

    private void initWidgets(View view) {
        teacherImage = view.findViewById(R.id.imageStudentChooseTeacher);
        textViewTeacherName = view.findViewById(R.id.textViewStudentChooseTeacherName);
        textViewTeacherCity = view.findViewById(R.id.textViewStudentChooseTeacherCity);
        textViewTeacherNumStudents = view.findViewById(R.id.textViewStudentChooseTeacherNumStudents);
        textViewTeacherGear = view.findViewById(R.id.textViewStudentChooseTeacherGear);
        textViewTeacherCar = view.findViewById(R.id.textViewStudentChooseTeacherCar);
        buttonChooseTeacher = view.findViewById(R.id.buttonStudentChooseTeacher);

        String teacherImageString = mTeacher.getImageUrl();
        if(teacherImage != null){
            Glide.with(getContext()).load(teacherImageString).into(teacherImage);
        } else {
            // TODO: this doesn't work
            teacherImage.setBackgroundResource(R.mipmap.ic_launcher_round);
        }
        textViewTeacherName.setText(String.format("Name: %s", mTeacher.getFullName()));
        textViewTeacherCity.setText(String.format("City: %s", mTeacher.getCity()));
        textViewTeacherNumStudents.setText(String.format("Number of students: %s", mTeacher.getCity()));
        textViewTeacherGear.setText(String.format("Gear type: %s", mTeacher.getGearType()));
        textViewTeacherCar.setText(String.format("Car: %s", mTeacher.getCarModel()));

        buttonChooseTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        FragmentManager fm = getFragmentManager();
        if(fm != null){
            dialogFragment.setTargetFragment(this, 0);
            fm.beginTransaction().add(dialogFragment, "dialog").commit();
        } else {
            unexpectedError();
        }
    }

    private void initChooseTeacherDialog() {
        Log.d(TAG, "in initChooseTeacherDialog");
        if(mStudent.hasTeacher()){
            dialogFragment = new PromptUserDialog();
        } else{
            dialogFragment = new PositiveNegativeDialog();
        }
        dialogFragment.setArguments(createArgsForDialog());
    }

    private Bundle createArgsForDialog() {
        Log.d(TAG, "in createArgsForChooseTeacherDialog");
        Bundle args = new Bundle();
        args.putString(BaseDialogFragment.ARG_TITLE,
                getString(R.string.choose_teacher_dialog_title));
        if(!mStudent.hasTeacher()){
            args.putString(BaseDialogFragment.ARG_MESSAGE,
                    getString(R.string.choose_teacher_dialog_message_no_teacher));
        } else {
            args.putString(BaseDialogFragment.ARG_MESSAGE,
                    getString(R.string.choose_teacher_dialog_message_has_teacher));
        }
        return args;
    }

    @Override
    public void onPositiveClick() {
        callBack.performUserAction();
    }

    @Override
    public void onNegativeClick() {

    }
}
