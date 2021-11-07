package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DisplayHelp extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Create the view
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.display_help, null);
        //Create button listener
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("TAG", "you clicked dialog buttonr");
            }
        };
        //Build the alert dialog
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.camera_help)
                .setTitle("Info de camara")
                .setView(v)
                .setPositiveButton(R.string.default_entendido, listener)
                .create();
    }
}
