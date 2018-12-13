package com.example.eliad.drive4u;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    // Tag for the Log
    private final static String TAG = "RegistrationActivity";

    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private EditText editTextPassword;
    // user home activity
    private Class<? extends AppCompatActivity> userHomeActivity;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        editTextName = findViewById(R.id.editTextRegistrationName);
        editTextPhone = findViewById(R.id.editTextRegistrationPhone);
        editTextEmail = findViewById(R.id.editTextRegistrationEmail);
        editTextPassword = findViewById(R.id.editTextRegistrationPassword);
    }

    public void signUp(View view) {
        Log.d(TAG, "in signUp");
        final String name = editTextName.getText().toString();
        final String phone = editTextPhone.getText().toString();
        final String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if(email.isEmpty()){
            Toast.makeText(this,getString(R.string.empty_email), Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.isEmpty()){
            Toast.makeText(this, getString(R.string.empty_password),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email ,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "registered successfully");

                            FirebaseUser newUser = mAuth.getCurrentUser();
                            assert newUser != null;
                            createNewUser(newUser, name, phone, email);
                            finish();
                            startActivity(new Intent(getApplicationContext(), userHomeActivity));
                        } else {
                            Log.d(TAG, "registration failed");
                        }
                    }
                });
    }

    private void createNewUser(@NonNull FirebaseUser newUser, String name, String phone, String email) {
        Log.d(TAG, "in createNewUser");
        String uId = newUser.getUid();
        Map<String, Object> params = new HashMap<>();
        params.put("id", uId);
        params.put("name", name);
        params.put("phone", phone);
        params.put("email", email);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String userType = extras.getString(getString(R.string.user_home_activity));
        assert userType != null;
        if(userType.equals(getString(R.string.student))){
            createNewStudent(params, uId);
        } else {
            createNewTeacher(params, uId);
        }


    }

    private void createNewStudent(Map<String, Object> params, String uId) {
        Log.d(TAG, "in createNewStudent");
        userHomeActivity = StudentHomeActivity.class;
        Student newStudent = new Student(params);
        db.collection("Students").document(uId).set(newStudent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "firestore student created successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "firestore student creation failed");
                    }
                });
    }

    private void createNewTeacher(Map<String, Object> params, String uId) {
        Log.d(TAG, "in createNewTeacher");
        userHomeActivity = TeacherHomeActivity.class;
        Teacher newTeacher = new Teacher(params);
        db.collection("Teachers").document(uId).set(newTeacher)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "firestore teacher created successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "firestore teacher creation failed");
                    }
                });
    }
}
