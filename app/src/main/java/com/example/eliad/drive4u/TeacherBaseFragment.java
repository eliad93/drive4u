package com.example.eliad.drive4u;

import android.os.Bundle;
import android.util.Log;

import com.example.eliad.drive4u.models.Teacher;

public class TeacherBaseFragment extends AppBaseFragment {

    private static final String TAG = AppBaseFragment.class.getName();

    public static final String ARG_TEACHER = TAG + ".arg_teacher";

    Teacher mTeacher;

    static protected Bundle getBaseBundle(Teacher teacher) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_TEACHER, teacher);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null ) {
            Log.d(TAG, "attempting to get the teacher");
            Teacher t = savedInstanceState.getParcelable(ARG_TEACHER);
            if (t != null) {
                mTeacher = t;
                Log.d(TAG, "Got the teacher");
            }
        }
    }
}
