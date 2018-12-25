package com.example.eliad.drive4u.activities;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.base_activities.StudentBaseActivity;
import com.example.eliad.drive4u.base_activities.TeacherBaseActivity;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity {
    // Tag for the Log
    private final static String TAG = LoginActivity.class.getName();

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // widgets
    private EditText editTextUserEmail;
    private EditText editTextPassword;
    private Button   buttonLogin;
    private TextView textViewNewUser;
    private ProgressBar progressBar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        // set all the widgets
        editTextUserEmail = findViewById(R.id.editTextUserEmail);
        editTextUserEmail.requestFocus();
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin      = findViewById(R.id.buttonLogin);
        textViewNewUser  = findViewById(R.id.textViewNewUserClick);
        progressBar      = findViewById(R.id.loginProgressBar);

        if(mAuth.getCurrentUser() != null){
            login();
        }
        // there is no need to see the progress bar as long as there is no blocking task.
        progressBar.setVisibility(View.GONE);

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
//                progressBar.setVisibility(View.VISIBLE);
                onNewUserClick();
//                progressBar.setVisibility(View.GONE);
                return false;
            }
        });
    }

    public void onLoginClick(){
        Log.d(TAG, "in onLoginClick");

        // let the user know there is progress
        progressBar.setVisibility(View.VISIBLE);

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
                            login();
                            Log.d(TAG, "signInWithEmail:success");
                        } else {
                            Log.d(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void onNewUserClick(){
        Log.d(TAG, "in onNewUserClick");
        // start a new activity for registration.
        Intent intent = new Intent(this, UserTypeChoiceActivity.class);
        finish();
        startActivity(intent);

    }
    public void login (){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        tryStudentLogin(user);
    }

    private void tryStudentLogin(final FirebaseUser user) {
        DocumentReference userDoc = db.collection("Students")
                .document(user.getUid());
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if(document.exists()){
                        Intent intent = new Intent(getApplicationContext(),
                                StudentHomeActivity.class);
                        Student student = document.toObject(Student.class);
                        intent.putExtra(StudentBaseActivity.ARG_STUDENT, student);
                        finish();
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "No such student");
                        tryTeacherLogin(user);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void tryTeacherLogin(FirebaseUser user) {
        DocumentReference userDoc = db.collection("Teachers")
                .document(user.getUid());
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if(document.exists()){
                        Intent intent = new Intent(getApplicationContext(),
                                TeacherHomeActivity.class);
                        Teacher teacher = document.toObject(Teacher.class);
                        intent.putExtra(TeacherBaseActivity.ARG_TEACHER, teacher);
                        finish();
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "No such teacher");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
