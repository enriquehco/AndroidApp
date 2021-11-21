package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myapplication.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**Diferentes acciones según el boton que se pulse en la pantalla principal, cada uno inicia las
    actividades que son respectivamente:
    -startMap: inicia la actividad de rutas
    -startQR: inicia la actividad de lectura de código qr
    -startStaffSearch: inicia la actividad de información de departamentos
     **/

    public void startMap(View view) {
        Intent intent = new Intent(this, DisplayRutasPosibles.class);
        startActivity(intent);
    }

    public void startQR(View view) {
        Intent intent = new Intent(this, DisplayQRActivity.class);
        startActivity(intent);
    }

    public void startStaffSearch(View view){
        Intent intent = new Intent( this, DisplayBuscaPersonal.class);
        startActivity(intent);
    }

    public void startAgenteConv(View view){
        Intent intent = new Intent( this, DisplayAgenteConversacional.class);
        startActivity(intent);
    }
}