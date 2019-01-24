package com.base.eliad.drive4u.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.base.eliad.drive4u.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserBaseFragment extends Fragment {

    private static final String TAG = UserBaseFragment.class.getName();

    public static final String ARG_USER = TAG + ".arg_teacher";

    protected User mUser;

    // Firebase
    protected FirebaseAuth mAuth;
    protected FirebaseUser fuser;
    protected FirebaseFirestore db;

    public UserBaseFragment() { }

    public static Bundle newInstanceBaseArgs(User user) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = getArguments().getParcelable(ARG_USER);
        } else {
            Log.d(TAG, "Created a TeacherBaseFragment with no teacher");
        }
        initDbVariables();
    }

    protected void initDbVariables() {
        Log.d(TAG, "get current firebase user");
        mAuth = FirebaseAuth.getInstance();
        fuser = mAuth.getCurrentUser();
        assert mUser != null;
        db = FirebaseFirestore.getInstance();
    }
}
