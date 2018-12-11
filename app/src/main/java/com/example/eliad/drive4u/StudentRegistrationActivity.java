package com.example.eliad.drive4u;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ResourceBundle;

public class StudentRegistrationActivity extends RegistrationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources resources = getResources();
        model = new RegistrationModel(resources.getString(R.string.regFormStudentHeader));
        binding.setRegistration(model);
    }

    @Override
    public void signUp(View view) {
        User newUser = new Student(model);
        registerNewUser(newUser);
    }

}
