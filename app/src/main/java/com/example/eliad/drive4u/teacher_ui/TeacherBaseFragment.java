package com.example.eliad.drive4u.teacher_ui;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.fragments.UnexpectedErrorDialog;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

abstract public class TeacherBaseFragment extends Fragment {

    private static final String TAG = TeacherBaseFragment.class.getName();

    public static final String ARG_TEACHER = TAG + ".arg_teacher";
    // shared preferences
    protected SharedPreferences sharedPreferences;
    // models
    protected Teacher mTeacher;
    // Firebase
    protected FirebaseAuth mAuth;
    protected FirebaseUser mUser;
    protected FirebaseFirestore db;

    public TeacherBaseFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        initDbVariables();
        sharedPreferences = getContext().getSharedPreferences(getString(R.string.app_name)
                + mUser.getUid(), Context.MODE_PRIVATE);
        initData();
    }

    protected void initDbVariables() {
        Log.d(TAG, "get current firebase user");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;
        db = FirebaseFirestore.getInstance();
    }

    private void initData() {
        Log.d(TAG, "initData");
        getTeacherFromSharedPreferences();
    }

    protected Boolean getTeacherFromSharedPreferences() {
        Log.d(TAG, "getTeacherFromSharedPreferences");
        Gson gson = new Gson();
        String json = sharedPreferences.getString(TeacherMainActivity.ARG_TEACHER, "");
        if(json != null &&!json.equals("")){
            mTeacher = gson.fromJson(json, Teacher.class);
            return true;
        }
        return false;
    }

    protected void unexpectedError(){
        Log.d(TAG, "unexpectedError");
        UnexpectedErrorDialog dialog = new UnexpectedErrorDialog();
        dialog.show(getChildFragmentManager(), "dialog");
    }

    protected CollectionReference getTeachersDb(){
        return db.collection(getString(R.string.DB_Teachers));
    }

    protected CollectionReference getStudentsDb(){
        return db.collection(getString(R.string.DB_Students));
    }

    protected DocumentReference getTeacherDoc(){
        return getTeachersDb().document(mTeacher.getID());
    }

    protected DocumentReference getStudentDoc(Student student){
        return getStudentsDb().document(student.getID());
    }
}
