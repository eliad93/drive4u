package com.example.eliad.drive4u;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TeacherRegistrationActivity extends RegistrationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources resources = getResources();
        model = new RegistrationModel(resources.getString(R.string.regFormTeacherHeader));
        binding.setRegistration(model);
    }

    @Override
    public void signUp(View view) {
        User newUser = new Teacher(model);
        registerNewUser(newUser);
    }
}
