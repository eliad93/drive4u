package com.example.eliad.drive4u.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.eliad.drive4u.R;

public class PositiveNegativeDialog extends BaseDialogFragment {
    // Tag for the Log
    private static final String TAG = PositiveNegativeDialog.class.getName();

    private OnClickDialogButton callBack;

    public interface OnClickDialogButton {
        public void onPositiveClick();
        public void onNegativeClick();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment f = getTargetFragment();
        if(f instanceof OnClickDialogButton){
            callBack = (OnClickDialogButton) f;
        } else {
            Log.d(TAG, "activity does not implement OnClickDialogButton");
        }
    }

    public PositiveNegativeDialog(){
        // Empty constructor required for DialogFragment
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = "no message", title = "no title";
        Resources resources = getResources();
        Bundle args = getArguments();
        if (args != null) {
            title = args.getString(ARG_TITLE);
            message = args.getString(ARG_MESSAGE);
        }
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(R.string.dialog_positive_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                callBack.onPositiveClick();
                            }
                        })
                .setNegativeButton(R.string.dialog_negative_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                callBack.onNegativeClick();
                            }
                        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
