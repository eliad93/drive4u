package com.base.eliad.drive4u.activities.student_activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.base_activities.StudentBaseActivity;
import com.base.eliad.drive4u.fragments.BaseDialogFragment;
import com.base.eliad.drive4u.fragments.PositiveNegativeDialog;
import com.base.eliad.drive4u.fragments.PromptUserDialog;
import com.base.eliad.drive4u.fragments.StudentChooseTeacherFragment;
import com.base.eliad.drive4u.helpers.ConditionsHelper;
import com.base.eliad.drive4u.models.Student;
import com.base.eliad.drive4u.models.Teacher;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.WriteBatch;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentChooseTeacherActivity extends StudentBaseActivity
        implements PositiveNegativeDialog.OnClickDialogButton {
    // Tag for the Log
    private static final String TAG = StudentChooseTeacherActivity.class.getName();
    public static final String ARG_TEACHER = TAG + Teacher.class.getName();
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
    StudentChooseTeacherFragment.ResultListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_choose_teacher);
        getSelectedTeacher();
        initWidgets();
        updateChooseTeacherDialog();
    }

    private void updateChooseTeacherDialog() {
        Log.d(TAG, "in initChooseTeacherDialog");
        Bundle args = new Bundle();
        args.putString(BaseDialogFragment.ARG_TITLE,
                getString(R.string.choose_teacher_dialog_title));
        if(mStudent.hasTeacher()){
            args.putString(BaseDialogFragment.ARG_MESSAGE,
                    getString(R.string.choose_teacher_dialog_message_no_teacher));
            dialogFragment = new PromptUserDialog();
        } else{
            args.putString(BaseDialogFragment.ARG_MESSAGE,
                    getString(R.string.choose_teacher_dialog_message_no_teacher));
            dialogFragment = new PositiveNegativeDialog();
            ((PositiveNegativeDialog) dialogFragment).setOnClickListener(this);
        }
        dialogFragment.setArguments(args);
    }

    private void initWidgets() {
        teacherImage = findViewById(R.id.imageStudentChooseTeacher);
        textViewTeacherName = findViewById(R.id.textViewStudentChooseTeacherName);
        textViewTeacherCity = findViewById(R.id.textViewStudentChooseTeacherCity);
        textViewTeacherNumStudents = findViewById(R.id.textViewStudentChooseTeacherNumStudents);
        textViewTeacherGear = findViewById(R.id.textViewStudentChooseTeacherGear);
        textViewTeacherCar = findViewById(R.id.textViewStudentChooseTeacherCar);
        buttonChooseTeacher = findViewById(R.id.buttonStudentChooseTeacher);

        String teacherImageString = mTeacher.getImageUrl();
        if(ConditionsHelper.imageStringValid(teacherImageString)){
            Glide.with(this).load(teacherImageString).into(teacherImage);
        } else {
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
                dialogFragment.show(getSupportFragmentManager(), "dialog");
            }
        });
    }

    private void getSelectedTeacher() {
        mTeacher = getIntent().getParcelableExtra(StudentChooseTeacherActivity.ARG_TEACHER);
        if(mTeacher == null){
            unexpectedError();
            finish();
        }
    }

    @Override
    public void onPositiveClick() {
        sendConnectionRequest();
        updateDataAfterRequest();
        Intent intent = new Intent(this, StudentSearchTeacherActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void onNegativeClick() {
    }
    private void sendConnectionRequest() {
        Log.d(TAG, "in sendConnectionRequest");
        DocumentReference mStudentDoc = getStudentDoc();
        // batch writes together
        WriteBatch batch = db.batch();
        // update student
        batch.update(mStudentDoc, "request",
                Student.ConnectionRequestStatus.SENT.getUserMessage());
        batch.update(mStudentDoc, "teacherId", mTeacher.getID());
        // update teacher
        DocumentReference teacher = getTeachersDb().document(mTeacher.getID());
        batch.update(teacher, "connectionRequests",
                FieldValue.arrayUnion(mStudent.getID()));
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "connection request to teacher sent");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "connection request to teacher failed");
            }
        });
    }

    private void updateDataAfterRequest() {
        Log.d(TAG, "in updateDataAfterRequest");
        mStudent.setTeacherId(mTeacher.getID());
        mStudent.setRequest(Student.ConnectionRequestStatus.SENT.getUserMessage());
        mTeacher.addConnectionRequest(mStudent.getID());
        updateChooseTeacherDialog();
        writeStudentToSharedPreferences();
        writeStudentTeacherToSharedPreferences(mTeacher);
    }
}
