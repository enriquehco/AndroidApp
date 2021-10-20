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
    private SensorManager sensorManager;
    Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_route_selector);
    }

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
        //if(routeSelected!=0) {
        Intent myIntent = new Intent(DisplayRutasPosibles.this, DisplayRuta.class);
        myIntent.putExtra("RouteSelected",Integer.toString(routeSelected));
        startActivity(myIntent);
        //}
    }
}
