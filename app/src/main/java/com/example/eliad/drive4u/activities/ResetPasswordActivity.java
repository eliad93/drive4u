package com.example.eliad.drive4u.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    Button btn;
    EditText send_email;

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        send_email = findViewById(R.id.editTextResetPass_Email);
        btn = findViewById(R.id.reset_email_btn);

        firebaseAuth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = send_email.getText().toString();
                if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    send_email.setError(getString(R.string.email_error));
                    return;
                }

                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this, "Please check your Email", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
