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

//Clase que extiende a DialogFragment para incluir los dialogos de ayuda que se implementan a lo
//largo de la aplicación para guiar al usuario si lo necesita
public class DisplayHelp extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Crea el listener del boton (funcion debug para ver como funcionaba el press del botón
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("TAG", "you clicked dialog buttonr");
            }
        };

        //Construye el dialogo de alerta construido (se puede modificar el contenido en las llamadas
        //propias que se hagan desde otras clases
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.camera_help)
                .setTitle("Info de camara")
                .setPositiveButton(R.string.default_entendido, listener)
                .create();
    }
}
