package com.example.eliad.drive4u.StudentUI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.eliad.drive4u.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentBaseFragment extends Fragment {
    private static final String TAG = StudentBaseFragment.class.getName();

    public static final String ARG_STUDENT = TAG + ".arg_student";

    protected Student mStudent;

    // Firebase
    protected FirebaseAuth mAuth;
    protected FirebaseUser mUser;
    protected FirebaseFirestore db;

    public static Bundle newInstanceBaseArgs(Student student) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_STUDENT, student);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStudent = getArguments().getParcelable(ARG_STUDENT);
        } else {
            Log.d(TAG, "Created a TeacherBaseFragment with no teacher");
        }
        initDbVariables();
    }

    protected void initDbVariables() {
        Log.d(TAG, "get current firebase user");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;
        db = FirebaseFirestore.getInstance();
    }
}