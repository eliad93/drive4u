package com.example.eliad.drive4u.registration;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.base_activities.StudentBaseActivity;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.User;
import com.example.eliad.drive4u.student_ui.StudentMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class StudentRegistrationFragment extends Fragment
        implements AdapterView.OnItemSelectedListener {
    private static final String TAG = StudentRegistrationFragment.class.getName();
    // shared preferences
    protected SharedPreferences sharedPreferences;
    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    // widgets
    protected EditText editTextName;
    protected EditText editTextPhone;
    protected EditText editTextCity;
    protected EditText editTextEmail;
    protected EditText editTextPassword;
    protected EditText editTextPasswordRepeat;
    protected ProgressBar progressBar;
    private Button btn;
    private Spinner spinnerGearType;

    private String gearType,name,firstName="", lastName="", phone,city,email, password, passwordRepeat;

    public StudentRegistrationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "in onCreateView");
        View view = inflater.inflate(R.layout.fragment_student_registration, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        progressBar  = view.findViewById(R.id.StudentRegistrationProgressBar);
        progressBar.setVisibility(View.GONE);

        btn = view.findViewById(R.id.buttonStudentRegistration);
        editTextName = view.findViewById(R.id.editTextStudentRegistrationName);
        editTextPhone = view.findViewById(R.id.editTextStudentRegistrationPhone);
        editTextEmail = view.findViewById(R.id.editTextStudentRegistrationEmail);
        editTextPassword = view.findViewById(R.id.editTextStudentRegistrationPassword);
        editTextPasswordRepeat = view.findViewById(R.id.editTextStudentRegistrationPasswordRepeat);
        editTextCity = view.findViewById(R.id.editTextStudentRegistrationCity);
        spinnerGearType = view.findViewById(R.id.spinnerChooseGearType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gear_types_student, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGearType.setAdapter(adapter);
        spinnerGearType.setOnItemSelectedListener(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(v);
            }
        });

        return view;
    }

    public void signUp(View view) {
        Log.d(TAG, "in signUp");
        initialize();
        if(!isValidInput()){
            Toast.makeText(getContext(), getString(R.string.signup_failed), Toast.LENGTH_SHORT).show();
        }else{
            signUpSuccess();
        }
    }

    private void signUpSuccess() {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "registered successfully");
                            FirebaseUser newUser = mAuth.getCurrentUser();
                            assert newUser != null;
                            createNewStudent(newUser, firstName, lastName, phone, city, email,gearType);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "registration failed" + task.getException());
                        }
                    }
                });
    }

    private boolean isValidInput(){
        Log.d(TAG, "isValidInput");
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
        if(gearType == null) {
            isValid = false;
        }
        return isValid;
    }

    private void createNewStudent(@NonNull FirebaseUser newUser, String firstName, String lastName,
                                  String phone, String city, String email, String gearType) {
        Log.d(TAG, "in createNewStudent");
        String uId = newUser.getUid();
        final Student newStudent = new Student(uId, firstName, lastName, phone, city,
                email, "", 0, 0, gearType,
                User.DEFAULT_IMAGE_KEY, User.OFFLINE);
        writeStudentToSharedPreferences(newStudent);
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
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "firestore student creation failed");
                    }
                });
        Intent intent = new Intent(getActivity().getApplicationContext(),
                StudentMainActivity.class);
        startActivity(intent);
    }

    private void initialize() {
        Log.d(TAG, "initialize");
        name = editTextName.getText().toString().trim();
        phone = editTextPhone.getText().toString().trim();
        city = editTextCity.getText().toString().trim();
        email = editTextEmail.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        passwordRepeat = editTextPasswordRepeat.getText().toString().trim();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected");
        gearType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        gearType = null;
    }

    @SuppressLint("ApplySharedPref")
    @SuppressWarnings("UnusedReturnValue")
    protected Boolean writeStudentToSharedPreferences(Student student) {
        if(sharedPreferences == null){
            sharedPreferences = getContext().getSharedPreferences(getString(R.string.app_name)
                    + student.getID(), Context.MODE_PRIVATE);
        }
        if(student != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(student);
            editor.putString(StudentBaseActivity.ARG_STUDENT, json);
            editor.commit();
            return true;
        }
        return false;
    }
}
