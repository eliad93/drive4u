package com.example.eliad.drive4u.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.base_activities.RegistrationBaseActivity;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
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

public class TeacherRegistrationActivity extends RegistrationBaseActivity
        implements AdapterView.OnItemSelectedListener {

    // Tag for the Log
    private static final String TAG = TeacherRegistrationActivity.class.getName();

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText editTextCarModel;
    private EditText editTextPrice;
    private Spinner spinnerGearType;

    private String gearType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        editTextFirstName = findViewById(R.id.editTextTeacherRegistrationFirstName);
        editTextLastName = findViewById(R.id.editTextTeacherRegistrationLastName);
        editTextPhone = findViewById(R.id.editTextTeacherRegistrationPhone);
        editTextEmail = findViewById(R.id.editTextTeacherRegistrationEmail);
        editTextPassword = findViewById(R.id.editTextTeacherRegistrationPassword);
        editTextCity = findViewById(R.id.editTextTeacherRegistrationCity);

        editTextCarModel = findViewById(R.id.editTextTeacherRegistrationCarModel);
        editTextPrice = findViewById(R.id.editTextTeacherRegistrationPrice);
        spinnerGearType = findViewById(R.id.spinnerChooseGearType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gear_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGearType.setAdapter(adapter);
        spinnerGearType.setOnItemSelectedListener(this);
    }

    public void signUp(View view) {
        Log.d(TAG, "in signUp");
        LinkedList<String> inputs = new LinkedList<>();
        final String firstName = getTextAndInsert(editTextFirstName, inputs);
        final String lastName = getTextAndInsert(editTextLastName, inputs);
        final String phone = getTextAndInsert(editTextPhone, inputs);
        final String city = getTextAndInsert(editTextCity, inputs);
        final String email = getTextAndInsert(editTextEmail, inputs);
        final String password = getTextAndInsert(editTextPassword, inputs);
        final String carModel = getTextAndInsert(editTextCarModel, inputs);
        final Integer price = Integer.valueOf(editTextPrice.getText().toString());

        if (!isValidInput(inputs)) {
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
                            createNewTeacher(newUser, firstName, lastName, phone, city, email,
                                    carModel, price, gearType);
                        } else {
                            Log.d(TAG, "registration failed " + task.getException());
                        }
                    }
                });
    }

    private boolean isValidInput(Collection<String> inputs) {
        if(gearType == null){
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            return false;
        }
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

    private void createNewTeacher(FirebaseUser newUser, String firstName, String lastName,
                                  String phone, String city, String email,
                                  String carModel, Integer price, String gearType) {
        Log.d(TAG, "in createNewTeacher");
        String uId = newUser.getUid();
        final Teacher newTeacher = new Teacher(uId, firstName, lastName, phone, city, email,
                carModel, price, gearType);
        db.collection(getResources().getString(R.string.DB_Teachers)).document(uId).set(newTeacher)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "firestore teacher created successfully");
                        Intent intent = new Intent(getApplicationContext(),
                                TeacherHomeActivity.class);
                        intent.putExtra("Teacher", newTeacher);
                        finish();
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "firestore teacher creation failed");
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        gearType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        gearType = null;
    }
}
