package com.example.eliad.drive4u.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eliad.drive4u.R;

public class UserTypeChoiceActivity extends AppCompatActivity {
    // Tag for the Log
    private final static String TAG = "UserTypeChoiceActivity";
    private float x1_student=0,x1_teacher=0,y1_student=0,y1_teacher=0;
    private float xSrart=0, xMove=0;
    private ObjectAnimator animator_student,animator_teacher;

    private TextView switch_student;
    private TextView switch_teacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_choice);
        switch_student = (TextView) findViewById(R.id.switch_student);
        switch_teacher = (TextView) findViewById(R.id.switch_teacher);
        x1_student= switch_student.getX();
        x1_teacher= switch_teacher.getX();
        switch_student.setTextColor(Color.TRANSPARENT);
        switch_teacher.setTextColor(Color.TRANSPARENT);
        animator_student = ObjectAnimator.ofInt(switch_student,"textColor",
                Color.TRANSPARENT, Color.GRAY, Color.TRANSPARENT);
        animator_teacher = ObjectAnimator.ofInt(switch_teacher,"textColor",
                Color.TRANSPARENT, Color.GRAY, Color.TRANSPARENT);
        blinkEffect();
    }

    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                xSrart = event.getX();
                xMove = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = event.getX();
                if(xMove-xSrart>5 || xMove - xSrart < -5) {
                    if (xMove > xSrart) {
                        switch_teacher.setX((int)(x1_teacher + (xMove - xSrart)));
                    } else {
                        switch_student.setX((int)(x1_student + (xMove - xSrart)));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(xMove-xSrart>20 || xMove - xSrart < -20) {
                    ChooseActivity();
                }
                 break;
        }
        return false;
    }
    private void ChooseActivity(){
        if(x1_student>switch_student.getX()){
            Log.d(TAG, "in studentRegistration");
            Intent intent = new Intent(this, StudentRegistrationActivity.class);
            finish();
            startActivity(intent);
        }else{
            Log.d(TAG, "in teacherRegistration");
            Intent intent = new Intent(this, TeacherRegistrationActivity.class);
            finish();
            startActivity(intent);
        }
    }

    private void blinkEffect(){
        animator_student.setDuration(1600);
        animator_student.setEvaluator(new ArgbEvaluator());
        animator_student.setRepeatMode(Animation.ABSOLUTE);
        animator_student.setRepeatCount(Animation.INFINITE);

        animator_teacher.setDuration(1600);
        animator_teacher.setEvaluator(new ArgbEvaluator());
        animator_teacher.setRepeatMode(Animation.ABSOLUTE);
        animator_teacher.setRepeatCount(Animation.INFINITE);
        animator_teacher.setStartDelay(800);

        animator_student.start();
        animator_teacher.start();
    }
}
