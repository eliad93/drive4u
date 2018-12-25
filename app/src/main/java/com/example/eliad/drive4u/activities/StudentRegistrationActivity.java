package com.example.eliad.drive4u.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.base_activities.RegistrationBaseActivity;
import com.example.eliad.drive4u.base_activities.StudentBaseActivity;
import com.example.eliad.drive4u.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;
import java.util.LinkedList;

public class StudentRegistrationActivity extends RegistrationBaseActivity {

    // Tag for the Log
    private static final String TAG = StudentSearchTeacherActivity.class.getName();

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        editTextFirstName = findViewById(R.id.editTextStudentRegistrationFirstName);
        editTextLastName = findViewById(R.id.editTextStudentRegistrationLastName);
        editTextPhone = findViewById(R.id.editTextStudentRegistrationPhone);
        editTextEmail = findViewById(R.id.editTextStudentRegistrationEmail);
        editTextPassword = findViewById(R.id.editTextStudentRegistrationPassword);
        editTextCity = findViewById(R.id.editTextStudentRegistrationCity);
        progressBar  = findViewById(R.id.StudentRegistrationProgressBar);

        progressBar.setVisibility(View.GONE);
    }

    public void signUp(View view) {
        Log.d(TAG, "in signUp");
        progressBar.setVisibility(View.VISIBLE);

        LinkedList<String> inputs = new LinkedList<>();
        final String firstName = getTextAndInsert(editTextFirstName, inputs);
        final String lastName = getTextAndInsert(editTextLastName, inputs);
        final String phone = getTextAndInsert(editTextPhone, inputs);
        final String city = getTextAndInsert(editTextCity, inputs);
        final String email = getTextAndInsert(editTextEmail, inputs);
        final String password = getTextAndInsert(editTextPassword, inputs);

        if (!isValidInput(inputs)) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "registered successfully");
                            FirebaseUser newUser = mAuth.getCurrentUser();
                            assert newUser != null;
                            createNewStudent(newUser, firstName, lastName, phone, city, email);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "registration failed" + task.getException());
                        }
                    }
                });
    }

    private boolean isValidInput(Collection<String> inputs) {
        for (String s : inputs) {
            if (s.isEmpty()) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private static String getTextAndInsert(EditText editText, LinkedList<String> list) {
        String s = editText.getText().toString();
        list.addLast(s);
        return s;
    }

    private void createNewStudent(FirebaseUser newUser, String firstName, String lastName,
                                  String phone, String city, String email) {
        Log.d(TAG, "in createNewStudent");
        String uId = newUser.getUid();
        final Student newStudent = new Student(uId, firstName, lastName, phone, city, email, "", 0, 0);
        db.collection("Students").document(uId).set(newStudent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "firestore student created successfully");
                        Intent intent = new Intent(getApplicationContext(),
                                StudentHomeActivity.class);
                        intent.putExtra(StudentBaseActivity.ARG_STUDENT, newStudent);

                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "firestore student creation failed");
                    }
                });
    }
}
