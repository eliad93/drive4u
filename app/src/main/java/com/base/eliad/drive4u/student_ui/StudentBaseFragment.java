package com.base.eliad.drive4u.student_ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.WindowManager;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.base_activities.StudentBaseActivity;
import com.base.eliad.drive4u.fragments.UnexpectedErrorDialog;
import com.base.eliad.drive4u.models.Student;
import com.base.eliad.drive4u.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

abstract public class StudentBaseFragment extends Fragment {
    private static final String TAG = StudentBaseFragment.class.getName();
    public static final String ARG_STUDENT = "student";
    // shared preferences
    protected SharedPreferences sharedPreferences;
    // models
    protected Student mStudent;
    // Firebase
    protected FirebaseAuth mAuth;
    protected FirebaseUser mUser;
    protected FirebaseFirestore db;

    public StudentBaseFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDbVariables();
        sharedPreferences = getContext().getSharedPreferences(getString(R.string.app_name)
                + mUser.getUid(), Context.MODE_PRIVATE);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    @Override
    public void onPause(){
        super.onPause();
        saveData();
    }

    protected void initDbVariables() {
        Log.d(TAG, "get current firebase user");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;
        db = FirebaseFirestore.getInstance();
    }

    private void initData() {
        getStudentFromSharedPreferences();
    }

    protected void updateData(){
        Log.d(TAG, "updateData");
        getStudentFromSharedPreferences();
    }

    protected void saveData() {
        writeStudentToSharedPreferences();
    }

    protected void unexpectedError(){
        Log.d(TAG, "unexpectedError");
        UnexpectedErrorDialog dialog = new UnexpectedErrorDialog();
        dialog.show(getChildFragmentManager(), "dialog");
    }

    @SuppressWarnings("UnusedReturnValue")
    protected Boolean getStudentFromSharedPreferences() {
        Log.d(TAG, "getStudentFromSharedPreferences");
        Gson gson = new Gson();
        String json = sharedPreferences.getString(StudentBaseActivity.ARG_STUDENT, "");
        if(json != null &&!json.equals("")){
            mStudent = gson.fromJson(json, Student.class);
            return true;
        }
        return false;
    }

    @SuppressLint("ApplySharedPref")
    @SuppressWarnings("UnusedReturnValue")
    protected Boolean writeStudentToSharedPreferences() {
        if(mStudent != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(mStudent);
            editor.putString(ARG_STUDENT, json);
            editor.commit();
            return true;
        }
        return false;
    }

    @SuppressLint("ApplySharedPref")
    @SuppressWarnings("UnusedReturnValue")
    protected Boolean writeStudentTeacherToSharedPreferences(Teacher teacher) {
        if(teacher != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(teacher);
            editor.putString(StudentBaseActivity.ARG_STUDENT, json);
            editor.commit();
            return true;
        }
        return false;
    }

    protected CollectionReference getTeachersDb(){
        return db.collection(getString(R.string.DB_Teachers));
    }

    protected CollectionReference getStudentsDb(){
        return db.collection(getString(R.string.DB_Students));
    }

    protected DocumentReference getTeacherDoc(Teacher teacher){
        return getTeacherDoc(teacher.getID());
    }

    protected DocumentReference getTeacherDoc(String teacherId){
        return getTeachersDb().document(teacherId);
    }

    protected DocumentReference getStudentDoc(){
        return getStudentsDb().document(mStudent.getID());
    }

    protected void disableUserInteraction() {
        Activity activity = getActivity();
        if(activity != null){
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    protected void enableUserInteraction() {
        Activity activity = getActivity();
        if(activity != null){
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
