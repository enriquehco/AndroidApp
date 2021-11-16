package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class DisplayRutasPosibles extends AppCompatActivity{

    private static final String TAG = "DisplayMapActivity";

    //onCreate inicializa lo esencial de la actividad para que comience a funcionar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_route_selector);
    }

    //click que se invoca desde cualquiera de los 3 botones de ruta (aunque en esta version lite
    //llevan a la misma ruta) donde dependiendo del boton decide que ruta se ha seleccionado.
    public void clickOnRoute(View view) {
        int routeSelected=0;
        switch (view.getId()){
            case R.id.Ruta1:
                routeSelected=0;
                break;
            case R.id.Ruta2:
                routeSelected=1;
                break;
            case R.id.Ruta3:
                routeSelected=2;
                break;
        }

        //Inicia la actividad de ruta dependiendo del boton de ruta seleccionado
        Intent myIntent = new Intent(DisplayRutasPosibles.this, DisplayRuta.class);
        myIntent.putExtra("RouteSelected",Integer.toString(routeSelected));
        startActivity(myIntent);
    }
}
