package com.example.eliad.drive4u.StudentUI;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.WindowManager;

import com.example.eliad.drive4u.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentBaseFragment extends Fragment {
    private static final String TAG = StudentBaseFragment.class.getName();

    public static final String ARG_STUDENT = TAG + ".arg_student";
    public static final String ARG_TEACHER = TAG + ".arg_teacher";

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
        Bundle arguments = getArguments();
        if (arguments != null) {
            mStudent = arguments.getParcelable(ARG_STUDENT);
        } else {
            Log.d(TAG, "Created a TeacherBaseFragment with no teacher");
        }
        initDbVariables();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStudent();
    }

    private void updateStudent(){
        Log.d(TAG, "updateStudent");
        db.collection("Students").document(mUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            if(document.exists()){
                                mStudent = document.toObject(Student.class);
                                Log.d(TAG, "updated student");
                            }
                        } else {
                            Log.d(TAG, "failed to update student");
                        }
                    }
                });
    }

    protected void initDbVariables() {
        Log.d(TAG, "get current firebase user");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        assert mUser != null;
        db = FirebaseFirestore.getInstance();
    }
}
