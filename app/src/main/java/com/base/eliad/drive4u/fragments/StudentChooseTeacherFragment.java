package com.base.eliad.drive4u.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.helpers.ConditionsHelper;
import com.base.eliad.drive4u.student_ui.StudentBaseFragment;
import com.base.eliad.drive4u.models.Student;
import com.base.eliad.drive4u.models.Teacher;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentChooseTeacherFragment extends StudentBaseFragment
        implements PositiveNegativeDialog.OnClickDialogButton {
    // Tag for the Log
    private static final String TAG = StudentChooseTeacherFragment.class.getName();
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
    ResultListener listener;

    public interface ResultListener{
        public void onResult(boolean b);
    }

    public void setResultListener(ResultListener resultListener){
        listener = resultListener;
    }

    public StudentChooseTeacherFragment() {
        // Required empty public constructor
    }

    public static StudentChooseTeacherFragment newInstance() {
        Log.d(TAG, "in newInstance");
        return new StudentChooseTeacherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSelectedTeacher();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_choose_teacher,
                container, false);
        initWidgets(view);
        updateChooseTeacherDialog();
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

        Context context;
        if(teacherImage != null && ((context=getContext())!=null)){
            String teacherImageString = mTeacher.getImageUrl();
            if(ConditionsHelper.imageStringValid(teacherImageString)){
                Glide.with(context).load(teacherImageString).into(teacherImage);
            } else {
                teacherImage.setBackgroundResource(R.mipmap.ic_launcher_round);
            }
        }

        textViewTeacherName.setText(String.format("Name: %s", mTeacher.getFullName()));
        textViewTeacherCity.setText(String.format("City: %s", mTeacher.getCity()));
        textViewTeacherNumStudents.setText(String.format("Number of students: %s", mTeacher.getCity()));
        textViewTeacherGear.setText(String.format("Gear type: %s", mTeacher.getGearType()));
        textViewTeacherCar.setText(String.format("Car: %s", mTeacher.getCarModel()));

        buttonChooseTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.show(getChildFragmentManager(), "dialog");
            }
        });
    }

    private void updateChooseTeacherDialog() {
        Log.d(TAG, "in initChooseTeacherDialog");
        if(mStudent.hasTeacher()){
            dialogFragment = new PromptUserDialog();
        } else{
            dialogFragment = new PositiveNegativeDialog();
            ((PositiveNegativeDialog) dialogFragment).setOnClickListener(this);
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
        sendConnectionRequest();
        updateDataAfterRequest();
        listener.onResult(true);
    }

    @Override
    public void onNegativeClick() {
        listener.onResult(false);
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

    @SuppressWarnings("UnusedReturnValue")
    protected Boolean getSelectedTeacher() {
        Log.d(TAG, "getStudentFromSharedPreferences");
        Gson gson = new Gson();
        String json = sharedPreferences.getString(ARG_TEACHER, "");
        if(json != null &&!json.equals("")){
            mTeacher = gson.fromJson(json, Teacher.class);
            return true;
        }
        return false;
    }
}
