package com.mp.test_cv;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class CheckDialog extends DialogFragment {
    public interface CheckDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    CheckDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (CheckDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("CheckDialog"
                    + " must implement NoticeDialogListener");
        }
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle scanedInfo = getArguments();
        String contextMessage =  "Caloires: "+ scanedInfo.getString("calories") +
                "\nCarbohydrate: " + scanedInfo.getString("carbohydrate") +
                "\nProten: " + scanedInfo.getString("protein") +
                "\nFat: " + scanedInfo.getString("fat") +
                "\nSaturFat: " + scanedInfo.getString("saturFat") +
                "\nSugars: " + scanedInfo.getString("sugar") +
                "\nSodium: " + scanedInfo.getString("sodium") +
                "\nDietaryFiber: " + scanedInfo.getString("dietaryFiber");

                builder.setMessage(contextMessage)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        listener.onDialogPositiveClick(CheckDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        listener.onDialogNegativeClick(CheckDialog.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}