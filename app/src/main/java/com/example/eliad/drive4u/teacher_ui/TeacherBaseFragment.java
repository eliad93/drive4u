package com.example.eliad.drive4u.teacher_ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.eliad.drive4u.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherBaseFragment extends Fragment {

    private static final String TAG = TeacherBaseFragment.class.getName();

    public static final String ARG_TEACHER = TAG + ".arg_teacher";

    protected Teacher mTeacher;
    // Firebase
    protected FirebaseAuth mAuth;
    protected FirebaseUser mUser;
    protected FirebaseFirestore db;

    public TeacherBaseFragment() { }

    public static Bundle newInstanceBaseArgs(Teacher teacher) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_TEACHER, teacher);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTeacher = getArguments().getParcelable(ARG_TEACHER);
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

    protected void unexpectedError(){

    }
}
