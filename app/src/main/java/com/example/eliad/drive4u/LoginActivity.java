package com.example.eliad.drive4u;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    final static String TAG = "LoginActivity";

    // widgets
    EditText editTextUserName;
    EditText editTextPassword;
    Button   buttonLogin;
    TextView textViewNewUser;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // set all the widgets
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin      = findViewById(R.id.buttonLogin);
        textViewNewUser  = findViewById(R.id.textViewNewUser);

        // set a callback for the login button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick();
            }
        });

        textViewNewUser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onNewUserTouch();
                return false;
            }
        });
    }

    public void onLoginClick(){
        Log.d(TAG, "in onLoginClick");
        String userName     = editTextUserName.getText().toString();
        String userPassword = editTextPassword.getText().toString();

        Log.d(TAG, "userName=" + userName + " userPassword=" + userPassword);

        Log.d(TAG, "checking if the user name is in the data base with the password");

        Log.d(TAG, "did not find the user, making a toast.");
        Toast.makeText(this, R.string.wrong_user_name_or_password, Toast.LENGTH_SHORT).show();
    }

    public void onNewUserTouch(){
        Log.d(TAG, "in onNewUserTouch");
        // start a new activity for registration.

        //Intent intent = new Intent(this, newUserActivity.class)

    }
}
