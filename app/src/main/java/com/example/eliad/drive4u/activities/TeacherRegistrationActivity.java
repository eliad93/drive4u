package com.example.eliad.drive4u.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.base_activities.RegistrationBaseActivity;
import com.example.eliad.drive4u.base_activities.TeacherBaseActivity;
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
import java.util.regex.Pattern;

public class TeacherRegistrationActivity extends RegistrationBaseActivity
        implements AdapterView.OnItemSelectedListener {

    // Tag for the Log
    private static final String TAG = TeacherRegistrationActivity.class.getName();

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText editTextCarModel,editTextPrice, editTextLessonLen;
    private Spinner spinnerGearType;

    private String gearType,name,firstName="", lastName="", phone,city,email, password, passwordRepeat, carModel, priceString,lessonLenString;
    Integer price, lessonLen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        progressBar  = findViewById(R.id.TeacherRegistrationProgressBar);
        progressBar.setVisibility(View.GONE);

        editTextName = findViewById(R.id.editTextTeacherRegistrationName);
        editTextPhone = findViewById(R.id.editTextTeacherRegistrationPhone);
        editTextEmail = findViewById(R.id.editTextTeacherRegistrationEmail);
        editTextPassword = findViewById(R.id.editTextTeacherRegistrationPassword);
        editTextPasswordRepeat = findViewById(R.id.editTextTeacherRegistrationPasswordRepeat);
        editTextCity = findViewById(R.id.editTextTeacherRegistrationCity);

        editTextCarModel = findViewById(R.id.editTextTeacherRegistrationCarModel);
        editTextPrice = findViewById(R.id.editTextTeacherRegistrationPrice);
        editTextLessonLen = findViewById(R.id.editTextTeacherRegistrationLessonLen);
        spinnerGearType = findViewById(R.id.spinnerChooseGearType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gear_types_teacher, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGearType.setAdapter(adapter);
        spinnerGearType.setOnItemSelectedListener(this);
    }

    public void signUp(View view) {
        Log.d(TAG, "in signUp");
        initialize();
        if(!isValidInput()){
            Toast.makeText(this, getString(R.string.signup_failed), Toast.LENGTH_SHORT).show();
        }else{
            signUpSuccess();
        }
    }

    private void signUpSuccess() {
        price = Integer.valueOf(priceString);
        lessonLen = Integer.valueOf(lessonLenString);
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "registered successfully");
                            FirebaseUser newUser = mAuth.getCurrentUser();
                            assert newUser != null;
                            createNewTeacher(newUser, firstName, lastName, phone, city, email,
                                    carModel, price, gearType, lessonLen);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "registration failed " + task.getException());
                        }
                    }
                });
    }

    private void initialize() {
        name = editTextName.getText().toString().trim();
        phone = editTextPhone.getText().toString().trim();
        city = editTextCity.getText().toString().trim();
        email = editTextEmail.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        passwordRepeat = editTextPasswordRepeat.getText().toString().trim();
        carModel = editTextCarModel.getText().toString().trim();
        priceString = editTextPrice.getText().toString().trim();
        lessonLenString = editTextLessonLen.getText().toString().trim();
    }

    private boolean isValidInput(){
        boolean isValid = true;
        if(name.isEmpty() || !name.contains(" ")) {
            editTextName.setError(getString(R.string.name_error));
            isValid = false;
        }else{
            firstName = name.split(" ", 2)[0];
            lastName = name.split(" ", 2)[1];
            if(firstName.isEmpty() || lastName.isEmpty()){
                editTextName.setError(getString(R.string.name_error));
                isValid = false;
            }
        }
        if(phone.isEmpty() || phone.length() != 10) {
            editTextPhone.setError(getString(R.string.phone_error));
            isValid = false;
        }
        if(city.isEmpty()) {
            editTextCity.setError(getString(R.string.city_error));
            isValid = false;
        }
        if(password.isEmpty()) {
            editTextPassword.setError(getString(R.string.passward_empty));
            isValid = false;
        }else {
            if (password.length() < 6) {
                editTextPassword.setError(getString(R.string.password_len_error));
                isValid = false;
            }
        }
        if(passwordRepeat.isEmpty()) {
            editTextPasswordRepeat.setError(getString(R.string.passward_empty));
            isValid = false;
        }else {
            if (!passwordRepeat.equals(password)) {
                editTextPasswordRepeat.setError(getString(R.string.passwords_diff));
                isValid = false;
            }
        }
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.email_error));
            isValid = false;
        }
        if(carModel.isEmpty()) {
            editTextCarModel.setError(getString(R.string.car_model_error));
            isValid = false;
        }
        if(priceString.isEmpty()) {
            editTextPrice.setError(getString(R.string.please_enter_price));
            isValid = false;
        }
        if(lessonLenString.isEmpty()) {
            editTextLessonLen.setError(getString(R.string.lesson_len_error));
            isValid = false;
        }
        if(gearType == null) {
            isValid = false;
        }
        return isValid;
    }


    private void createNewTeacher(FirebaseUser newUser, String firstName, String lastName,
                                  String phone, String city, String email,
                                  String carModel, Integer price, String gearType, Integer lessonLen) {
        Log.d(TAG, "in createNewTeacher");
        InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        String uId = newUser.getUid();
        final Teacher newTeacher = new Teacher(uId, firstName, lastName, phone, city, email,
                carModel, price, gearType,lessonLen);
        db.collection(getResources().getString(R.string.DB_Teachers)).document(uId).set(newTeacher)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "firestore teacher created successfully");
                        Intent intent = new Intent(getApplicationContext(),
                                TeacherHomeActivity.class);
                        intent.putExtra(TeacherBaseActivity.ARG_TEACHER, newTeacher);
                        finish();
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, UserTypeChoiceActivity.class);
        finish();
        startActivity(intent);
    }
}
