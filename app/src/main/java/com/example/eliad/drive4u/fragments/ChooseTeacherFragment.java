package com.example.eliad.drive4u.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.eliad.drive4u.R;

public class ChooseTeacherFragment extends DialogFragment {
    private Boolean mCanChooseTeacher;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCanChooseTeacher = getArguments().getBoolean("canChooseTeacher");
        String message;
        if(mCanChooseTeacher){
            message = "connection request sent"; // TODO: more info
        } else {
            message = "Already has a teacher, disconnect first.";
        }
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message) // TODO: better message
                .setTitle("teacher choose")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
