package com.example.eliad.drive4u.Notifications;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    private static final String TAG = MyFirebaseIdService.class.getName();

    @Override
    public void onTokenRefresh() {
        Log.d(TAG,"onTokenRefresh");
        super.onTokenRefresh();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        if (firebaseUser != null) {
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken) {
        Log.d(TAG,"updateToken");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Token.TOKEN_PATH);
        Token token = new Token(refreshToken);
        Log.d(TAG, "updating TOKEN for " + firebaseUser.getEmail() + " with new TOKEN: " + refreshToken);
        reference.child(firebaseUser.getUid()).setValue(token);
    }
}
