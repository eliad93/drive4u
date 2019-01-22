package com.example.eliad.drive4u.base_activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.fragments.PromptUserDialog;
import com.example.eliad.drive4u.fragments.UnexpectedErrorDialog;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.example.eliad.drive4u.teacher_ui.TeacherMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

@SuppressLint("Registered")
abstract public class TeacherBaseActivity extends AppCompatActivity {
    // Tag for the Log
    private static final String TAG = TeacherBaseActivity.class.getName();
    // shared preferences
    SharedPreferences sharedPreferences;
    // key for passing the teacher between activities
    public static final String ARG_TEACHER = Teacher.class.getName();
    // the user
    protected Teacher mTeacher;
    // Firebase
    protected FirebaseAuth mAuth;
    protected FirebaseUser mUser;
    protected FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDbVariables();
        sharedPreferences = getSharedPreferences(getString(R.string.app_name)
                + mUser.getUid(), Context.MODE_PRIVATE);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }

    @Override
    protected void onPause() {
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
        getTeacherFromSharedPreferences();
    }

    protected void updateData() {
        getTeacherFromSharedPreferences();
    }

    protected void saveData() {
        writeTeacherToSharedPreferences();
    }

    @SuppressLint("ApplySharedPref")
    @SuppressWarnings("UnusedReturnValue")
    protected Boolean writeTeacherToSharedPreferences() {
        if(mTeacher != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(mTeacher);
            editor.putString(ARG_TEACHER, json);
            editor.commit();
            return true;
        }
        return false;
    }


    @NonNull
    @SuppressWarnings("UnusedReturnValue")
    private Boolean getTeacherFromSharedPreferences() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(ARG_TEACHER, "");
        if(json != null &&!json.equals("")){
            mTeacher = gson.fromJson(json, Teacher.class);
            return true;
        }
        return false;
    }

    protected void disableUserInteraction() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    protected void enableUserInteraction() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    protected void unexpectedError(){
        UnexpectedErrorDialog dialog = new UnexpectedErrorDialog();
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    /*
        all teacher activities should update their teacher
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                mTeacher = data.getParcelableExtra(ARG_TEACHER);
            }
        }
    }

    protected void myStartActivity(Class<? extends  AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(ARG_TEACHER, mTeacher);
        startActivity(intent);
    }

    protected void myStartActivityForResult(Class<? extends  AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(ARG_TEACHER, mTeacher);
        startActivityForResult(intent, 1);
    }

    protected void promptUserWithDialog(String title, String message){
        PromptUserDialog dialog = new PromptUserDialog();
        Bundle args = new Bundle();
        args.putString(PromptUserDialog.ARG_TITLE, title);
        args.putString(PromptUserDialog.ARG_MESSAGE, message);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "promptUserWithDialog");
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
