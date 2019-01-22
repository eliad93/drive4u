package com.example.eliad.drive4u.registration;


import android.annotation.SuppressLint;
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
import com.example.eliad.drive4u.base_activities.TeacherBaseActivity;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.student_ui.StudentMainActivity;
import com.example.eliad.drive4u.teacher_ui.TeacherMainActivity;
import com.example.eliad.drive4u.models.Teacher;
import com.example.eliad.drive4u.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherRegistrationFragment extends Fragment
        implements AdapterView.OnItemSelectedListener {
    // Tag for the Log
    private static final String TAG = TeacherRegistrationFragment.class.getName();
    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    // widgets
    private EditText editTextCarModel, editTextPrice, editTextLessonLen;
    private Spinner spinnerGearType;
    private Button btn;
    protected EditText editTextName;
    protected EditText editTextPhone;
    protected EditText editTextCity;
    protected EditText editTextEmail;
    protected EditText editTextPassword;
    protected EditText editTextPasswordRepeat;
    protected ProgressBar progressBar;

    private String gearType,name, firstName="", lastName="",
            phone, city,email, password, passwordRepeat,
            carModel, priceString, lessonLenString;
    Integer price, lessonLen;
    private SharedPreferences sharedPreferences;

    public TeacherRegistrationFragment() {
        Log.d(TAG, "empty constructor");
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view =  inflater.inflate(R.layout.fragment_teacher_registration, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        progressBar  = view.findViewById(R.id.TeacherRegistrationProgressBar);
        progressBar.setVisibility(View.GONE);

        btn = view.findViewById(R.id.buttonTeacherRegistration);
        editTextName = view.findViewById(R.id.editTextTeacherRegistrationName);
        editTextPhone = view.findViewById(R.id.editTextTeacherRegistrationPhone);
        editTextEmail = view.findViewById(R.id.editTextTeacherRegistrationEmail);
        editTextPassword = view.findViewById(R.id.editTextTeacherRegistrationPassword);
        editTextPasswordRepeat = view.findViewById(R.id.editTextTeacherRegistrationPasswordRepeat);
        editTextCity = view.findViewById(R.id.editTextTeacherRegistrationCity);

        editTextCarModel = view.findViewById(R.id.editTextTeacherRegistrationCarModel);
        editTextPrice = view.findViewById(R.id.editTextTeacherRegistrationPrice);
        editTextLessonLen = view.findViewById(R.id.editTextTeacherRegistrationLessonLen);
        spinnerGearType = view.findViewById(R.id.spinnerChooseGearType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gear_types_teacher, android.R.layout.simple_spinner_item);
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
        Log.d(TAG, "signUpSuccess");
        price = Integer.valueOf(priceString);
        lessonLen = Integer.valueOf(lessonLenString);
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "registered successfully");
                            FirebaseUser newUser = mAuth.getCurrentUser();
                            assert newUser != null;
                            createNewTeacher(newUser);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "registration failed " + task.getException());
                        }
                    }
                });
    }

    private void initialize() {
        Log.d(TAG, "initialize");
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
        } else if(Integer.valueOf(lessonLenString) > 60
                || Integer.valueOf(lessonLenString) < 30){
            editTextLessonLen.setError(getString(R.string.lesson_len_range_error));
            isValid = false;
        }
        if(gearType == null) {
            isValid = false;
        }
        return isValid;
    }

    private void createNewTeacher(FirebaseUser newUser) {
        Log.d(TAG, "in createNewTeacher");
        String uId = newUser.getUid();
        final Teacher newTeacher = new Teacher(uId, firstName, lastName, phone, city, email,
                carModel, price, gearType,lessonLen, User.DEFAULT_IMAGE_KEY, User.ONLINE);
        writeTeacherToSharedPreferences(newTeacher);
        db.collection(getResources().getString(R.string.DB_Teachers)).document(uId).set(newTeacher)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "firestore teacher created successfully");
                        Intent intent = new Intent(getContext(),
                                TeacherMainActivity.class);
                        intent.putExtra(TeacherMainActivity.ARG_TEACHER, newTeacher);
                        getActivity().finish();
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
        Log.d(TAG, "onItemSelected");
        gearType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        gearType = null;
    }

    @SuppressLint("ApplySharedPref")
    @SuppressWarnings("UnusedReturnValue")
    protected Boolean writeTeacherToSharedPreferences(Teacher teacher) {
        if(sharedPreferences == null){
            sharedPreferences = getContext().getSharedPreferences(getString(R.string.app_name)
                    + teacher.getID(), Context.MODE_PRIVATE);
        }
        if(teacher != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(teacher);
            editor.putString(TeacherBaseActivity.ARG_TEACHER, json);
            editor.commit();
            return true;
        }
        return false;
    }
}
