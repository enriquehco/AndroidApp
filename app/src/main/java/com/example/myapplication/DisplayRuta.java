package com.example.myapplication;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

public class DisplayRuta extends AppCompatActivity {

    private int routeselected;
    private int numImagen;
    private String imagenActual;

    SensorManager sensorManager;
    Sensor proximitySensor;
    Sensor rotationSensor;
    private Bitmap SOURCE_BITMAP;
    private int START_X = 2000;
    private int START_Y = 0;
    private int WIDTH_PX = 1000;
    private int HEIGHT_PX = 1650;
    private float X_ANTERIOR=100, Y_ANTERIOR=100;
    private boolean inicializa = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        routeselected = 0;
        numImagen = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_route);
        routeselected = Integer.parseInt(getIntent().getStringExtra("RouteSelected"));
        numImagen = 0;
        newImage();
        showImage();


        // calling sensor service.
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // from sensor service we are
        // calling proximity sensor
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // handling the case if the proximity
        // sensor is not present in users device.
        if (proximitySensor == null) {
            Toast.makeText(this, "No proximity sensor found in device.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // registering our sensor with sensor manager.
            sensorManager.registerListener(proximitySensorEventListener,proximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (rotationSensor == null) {
            Toast.makeText(this, "No rotation sensor found in device.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // registering our sensor with sensor manager.
            sensorManager.registerListener(rotationSensorEventListener, rotationSensor,  500 * 1000);

        }
    }

    public void newImage(){
        imagenActual = "drawable/ruta"+routeselected+"_"+numImagen;
        Context context = getApplicationContext();
        int id = context.getResources().getIdentifier(imagenActual, null, context.getPackageName());
        SOURCE_BITMAP = BitmapFactory.decodeResource(getResources(), id);
    }

    public void showImage(){
        if(START_X<0){START_X=0;}
        if(START_X+WIDTH_PX>4000){START_X=4000-WIDTH_PX;}


        Bitmap newBitmap = Bitmap.createBitmap(SOURCE_BITMAP, START_X, START_Y, WIDTH_PX, HEIGHT_PX, null, false);
        ImageView image = (ImageView)findViewById(R.id.imageViewRoute);
        image.setImageBitmap(newBitmap);
    }

    public boolean calculateMotion(float rotX, float rotY, int distance){
        int desp;
        boolean modif = false;
        //Nos estamos moviendo a la derecha
        float diff = Math.abs(X_ANTERIOR-rotX);
        if(diff>0.2) {
            modif = true;
            if (X_ANTERIOR < rotX) {
                //Nos estamos moviendo a la derecha
                if (X_ANTERIOR > 0 && X_ANTERIOR < 50 && rotX < 360 && rotX > 310) {
                    desp = -distance;
                } else {
                    desp = distance;
                }
            } else {
                if (rotX > 0 && rotX < 50 && X_ANTERIOR < 360 && X_ANTERIOR > 310) {
                    desp = distance;
                } else {
                    desp = -distance;
                }
            }
            X_ANTERIOR = rotX;
            START_X -= desp*(diff*0.5);
        }
        return modif;
    }

    SensorEventListener proximitySensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // method to check accuracy changed in sensor.
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // check if the sensor type is proximity sensor.
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0) {
                    numImagen++;
                    //calculateMotion(140,140,1);
                    //START_X = START_X + 100;
                    newImage();
                    showImage();
                }
            }
        }
    };

    SensorEventListener rotationSensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor == rotationSensor) {
                if (event.values.length > 4) {
                    float[] truncatedRotationVector = new float[4];
                    System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                    update(truncatedRotationVector);
                } else {
                    update(event.values);
                }
            }
        }

        private void update(float[] vectors){
            try
            {
                Thread.sleep(0);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
            int FROM_RADS_TO_DEGS = -57;
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
            int worldAxisX = SensorManager.AXIS_X;
            int worldAxisZ = SensorManager.AXIS_Z;
            float[] adjustedRotationMatrix = new float[9];
            SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
            float[] orientation = new float[3];
            SensorManager.getOrientation(adjustedRotationMatrix, orientation);
            float rotX = orientation[0] * FROM_RADS_TO_DEGS;
            float rotY = orientation[1] * FROM_RADS_TO_DEGS;
            if(rotX<0){rotX = 180+(180+rotX); }
            if(rotY<0){rotY = 180+(180+rotY); }
            if(inicializa) {
                X_ANTERIOR = rotX;
                inicializa=false;
            }
            ((TextView)findViewById(R.id.textView2)).setText(""+rotX+","+rotY);
            boolean modif = calculateMotion(rotX,rotY, 50);
            if(modif)
                showImage();
        }

    };


}

