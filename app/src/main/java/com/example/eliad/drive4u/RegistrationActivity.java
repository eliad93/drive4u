package com.example.eliad.drive4u;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.eliad.drive4u.databinding.RegistrationBaseBinding;


public abstract class RegistrationActivity extends AppCompatActivity {
    protected RegistrationBaseBinding binding;
    protected RegistrationModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil
                .setContentView(this, R.layout.registration_base);
    }

    public void signUp(View view){
        // this shouldn't get here
        throw new UnsupportedOperationException();
    }

    public static void registerNewUser(User u){
        // TODO: implement user registration
        throw new UnsupportedOperationException();
    }
}
