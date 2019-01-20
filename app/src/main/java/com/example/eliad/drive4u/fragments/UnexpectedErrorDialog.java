package com.example.eliad.drive4u.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.eliad.drive4u.R;

public class UnexpectedErrorDialog extends DialogFragment {
    // Tag for the Log
    private static final String TAG = UnexpectedErrorDialog.class.getName();

    public UnexpectedErrorDialog(){
        // Empty constructor required for DialogFragment
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.user_message_unexpected_error))
                .setTitle(getString(R.string.unexpected_error))
                .setNeutralButton(getString(R.string.dialog_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dismiss();
                            }
                        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
