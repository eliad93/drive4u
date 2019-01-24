package com.base.eliad.drive4u.base_activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.fragments.PromptUserDialog;
import com.base.eliad.drive4u.fragments.UnexpectedErrorDialog;
import com.base.eliad.drive4u.models.Student;
import com.base.eliad.drive4u.models.Teacher;
import com.base.eliad.drive4u.models.User;
import com.base.eliad.drive4u.activities.student_activities.StudentMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressLint("Registered")
abstract public class StudentBaseActivity extends AppCompatActivity {
    // Tag for the Log
    private static final String TAG = StudentBaseActivity.class.getName();
    // shared preferences
    SharedPreferences sharedPreferences;
    // Bundle arguments
    public static String ARG_STUDENT = Student.class.getName();
    // the user
    protected Student mStudent;
    // Firebase
    protected FirebaseAuth mAuth;
    protected FirebaseUser mUser;
    protected FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in onCreate");
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
        Log.d(TAG, "in initDbVariables");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;
        db = FirebaseFirestore.getInstance();
    }

    protected void initData() {
        Log.d(TAG, "initData");
        getStudentFromSharedPreferences();
    }

    @SuppressWarnings("UnusedReturnValue")
    protected Boolean getStudentFromSharedPreferences() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(StudentMainActivity.ARG_STUDENT, "");
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
            editor.putString(StudentMainActivity.ARG_STUDENT, json);
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

    protected void disableUserInteraction() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    protected void enableUserInteraction() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    protected void myStartActivity(Class<? extends  AppCompatActivity> activity) {
        Log.d(TAG, "in myStartActivity");
        writeStudentToSharedPreferences();
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    protected void updateData() {
        getStudentFromSharedPreferences();
    }

    protected void saveData() {
        writeStudentToSharedPreferences();
    }

    protected void unexpectedError(){
        UnexpectedErrorDialog dialog = new UnexpectedErrorDialog();
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    protected void setStudentImage(CircleImageView drawerImage){
        if(mStudent != null){
            if (mStudent.getImageUrl() == null ||
                    mStudent.getImageUrl().equals(User.DEFAULT_IMAGE_KEY)) {
                drawerImage.setImageResource(R.mipmap.ic_launcher);
            } else {
                Glide.with(getApplicationContext()).load(mStudent.getImageUrl()).into(drawerImage);
            }
        }
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

    protected DocumentReference getTeacherDoc(Teacher teacher){
        return getTeachersDb().document(teacher.getID());
    }

    protected DocumentReference getStudentDoc(){
        return getStudentsDb().document(mStudent.getID());
    }

    protected void resizeFragment(Fragment fragment, double widthPercent, double heightPercent) {
        if (fragment != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            View view = fragment.getView();
            WindowManager windowManager = getWindowManager();
            if(windowManager == null){
                return;
            }
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;
            int width = (int) (screenWidth * widthPercent);
            int height = (int) (screenHeight * heightPercent);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = width;
            params.height = height;
        }
    }
}
