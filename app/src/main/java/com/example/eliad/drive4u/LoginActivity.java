package com.example.eliad.drive4u;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Tag for the Log
    private final static String TAG = "LoginActivity";

    // Firebase
    private FirebaseAuth mAuth;

    // widgets
    private EditText editTextUserEmail;
    private EditText editTextPassword;
    private Button   buttonLogin;
    private TextView textViewNewUser;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        // set all the widgets
        editTextUserEmail = findViewById(R.id.editTextUserEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin      = findViewById(R.id.buttonLogin);
        textViewNewUser  = findViewById(R.id.textViewNewUserClick);

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
                onNewUserClick();
                return false;
            }
        });
    }

    public void onLoginClick(){
        Log.d(TAG, "in onLoginClick");
        String userEmail     = editTextUserEmail.getText().toString();
        String userPassword = editTextPassword.getText().toString();

        Log.d(TAG, "userName=" + userEmail + " userPassword=" + userPassword);

        Log.d(TAG, "checking if the user name is in the data base with the password");
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                        } else {
                            Log.d(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onNewUserClick(){
        Log.d(TAG, "in onNewUserClick");
        // start a new activity for registration.
        Intent intent = new Intent(this, UserTypeChoiceActivity.class);
        startActivity(intent);

        //Intent intent = new Intent(this, newUserActivity.class)

    }
}
