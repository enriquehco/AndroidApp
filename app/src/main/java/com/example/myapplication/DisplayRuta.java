package com.example.myapplication;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.util.concurrent.TimeUnit;


/*
* Esta clase se encarga de manejar los listener de los distintos sensores asi como de utilizar
* la clase gestor de rutas para ir gestionando como se van a mostrar las imagenes en función
* de los inputs recibidos por los sensores
* */
public class DisplayRuta extends AppCompatActivity {
    //Objeto que controla las imagenes de las distintas rutas
    GestorRutas gestorRutas;
    //Gestor de sensores
    SensorManager sensorManager;
    //Sensor de proximidad
    Sensor proximitySensor;
    //Sensor de rotacion
    Sensor rotationSensor;

    private TextToSpeech textToSpeechEngine;
    private boolean volumenActivo;
    Button btn;

    private int START_X = 0;
    private int START_Y = 0;

    private float X_ANTERIOR=100, Y_ANTERIOR=100;
    private boolean inicializa = true;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_route);

        textToSpeechEngine = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.SUCCESS) {
                    Log.e("TTS", "Inicio de la síntesis fallido");
                }
            }
        });
        Button btn = (Button) findViewById(R.id.volume);
        volumenActivo=false;
        btn.setBackgroundResource(R.drawable.voice_off);
        int routeSelected = Integer.parseInt(getIntent().getStringExtra("RouteSelected"));
        //Inicializamos el gestor de imagenes indicandole la ruta seleccionada
        gestorRutas = new GestorRutas(routeSelected);
        //Le decimos que busque la imagen correspondiente
        gestorRutas.newImage(getApplicationContext(),getResources());
        //Y la muestre en el imageViewRoute

        START_X = gestorRutas.getBestXStart();
        gestorRutas.showImage((ImageView)findViewById(R.id.imageViewRoute),START_X,START_Y);


        // Creamos el servicio de gestor de sensores
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Inicializamos y vinculamos los sensores
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);


        //Comprobamos que funcione el sensor de proximidad o que el dispositivo lo tenga
        if (proximitySensor == null) {
            //Si no funciona mostramos un mensaje y no hacemos nada
            Toast.makeText(this, "No proximity sensor found in device.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // En caso de que exista lo activamos y iniciamos el Listener
            sensorManager.registerListener(proximitySensorEventListener,proximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
        //Comprobamos que funcione el sensor de rotacion o que el dispositivo lo tenga
        if (rotationSensor == null) {
            //Si no funciona mostramos un mensaje y no hacemos nada
            Toast.makeText(this, "No rotation sensor found in device.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // En caso de que exista lo activamos y iniciamos el Listener
            sensorManager.registerListener(rotationSensorEventListener, rotationSensor,  500 * 1000);
        }

        setupVoice();
        talk();
    }

    /*
    * Este metodo se encargara de detectar si se hace un swipe hacia la
    * derecha o hacia la izquierda para ir a la imagen anterior o a la
    * siguiente respectivamente
    * */
    public void talk(){
        if(volumenActivo) {
            if (!gestorRutas.getInfo().isEmpty()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeechEngine.speak(gestorRutas.getInfo(), TextToSpeech.QUEUE_FLUSH, null, "tts1");
                }
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            //Cuando ponemos el dedo en la pantalla guardamos la posicion
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            //Cuando levantamos el dedo de la pantalla tomamos la posicion y realizamos lo
            //correspondiente
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                //Calculamos la distancia del swipe
                float deltaX = x2 - x1;
                //Si la distancia es mayor que MIN_DISTANCE y positiva
                //significa que hemos hecho swipe a la derecha
                if (deltaX > MIN_DISTANCE)
                {
                    //Pasamos a la foto anterior y mostramos la nueva imagen
                    boolean cambio = gestorRutas.ImagenAnterior(getApplicationContext(),getResources());
                    if(cambio) {
                        START_X = gestorRutas.getBestXStart();
                        gestorRutas.showImage((ImageView) findViewById(R.id.imageViewRoute), START_X, START_Y);
                        talk();
                    }
                }
                //Si la distancia es mayor que MIN_DISTANCE y negativa
                //significa que hemos hecho swipe a la izquierda
                else if(deltaX < -MIN_DISTANCE)
                {
                    //Pasamos a la siguiente foto y mostramos la nueva imagen
                    boolean cambio = gestorRutas.SiguienteImagen(getApplicationContext(),getResources());
                    if(cambio) {
                        START_X = gestorRutas.getBestXStart();
                        gestorRutas.showImage((ImageView) findViewById(R.id.imageViewRoute), START_X, START_Y);
                        talk();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }
    /*
    * Este metodo se basa en calcular como va a afectar un movimiento rotacional del movil
    * a la foto, es decir si la foto se va a desplazar a la izquierda o a la derecha
    * */
    public boolean calculateMotion(float posX, float rotY, int distance){
        int desp;
        boolean modif = false;
        //Calculamos la diferencia entre la posicion anterior y la actual
        float diff = Math.abs(X_ANTERIOR-posX);
        //Si la diferencia es menor que 0.2 la consideramos despreciable, de esta manera
        //reducimos la sensiblidad asi como la carga de trabajo del movil
        if(diff>0.2) {
            modif = true;

            if (X_ANTERIOR < posX) {
                //Si hemos pasado de [0-50] a [310-360] nos hemos movido a la izquierda
                if (X_ANTERIOR > 0 && X_ANTERIOR < 50 && posX < 360 && posX > 310) {
                    desp = -distance;
                }
                //Nos hemos movido a la derecha
                else {
                    desp = distance;
                }
            } else {
                //Si hemos pasado de [310-360] a [0-50] nos hemos movido a la derecha
                if (posX > 0 && posX < 50 && X_ANTERIOR < 360 && X_ANTERIOR > 310) {
                    desp = distance;
                }
                //Nos hemos movido a la izquierda
                else {
                    desp = -distance;
                }
            }
            //Actualizamos X_ANTERIOR
            X_ANTERIOR = posX;
            //Modificamos el punto de inicio de la foto, aplicando un factor
            //multiplicativo para darle suavidad
            START_X -= desp*(diff*0.5);
        }
        return modif;
    }
    /*
    * Listener que se activara cada vez que el sensor de proximidad cambie su valor
    * */
    SensorEventListener proximitySensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // method to check accuracy changed in sensor.
        }

        /*
        * Metodo donde decidimos como actuar en caso de que el sensor de cercania sea
        * modificado
        * */
        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                //Si el evento es igual a 0 quiere decir que el sensor de cercania a notado
                //que algo se a acercado a el
                if (event.values[0] == 0) {
                    //En ese caso pasamos a la siguiente imagen y la mostramos
                    boolean cambio = gestorRutas.SiguienteImagen(getApplicationContext(),getResources());
                    if(cambio) {
                        START_X = gestorRutas.getBestXStart();
                        gestorRutas.showImage((ImageView) findViewById(R.id.imageViewRoute), START_X, START_Y);
                        talk();
                    }
                }
            }
        }
    };
    /*
     * Listener que se activara cada vez que el sensor de rotacion muestree en funcion
     * del parametro que le hemos pasado al crearlo
     * */
    SensorEventListener rotationSensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
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

        /*
         * Metodo donde decidimos como actuar en caso de que el sensor de rotacion sea
         * modificado
         * */
        private void update(float[] vectors){
            //Si lo consideramos conveniente por que el dispositivo no
            //puede soportar la carga de trabajo, dormimos la hebra cierto
            //tiempo para que no se sature debido a la sensiblidad del sensor
            try{
                Thread.sleep(0);
            }catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }
            int FROM_RADS_TO_DEGS = -57;
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
            int worldAxisX = SensorManager.AXIS_X;
            int worldAxisZ = SensorManager.AXIS_Z;
            float[] adjustedRotationMatrix = new float[9];
            SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
            //Vector donde cargaremos la direccion del dispositivo
            float[] orientation = new float[3];
            SensorManager.getOrientation(adjustedRotationMatrix, orientation);
            //Direcciones de la x e y del dispositivo en grados
            float posX = orientation[0] * FROM_RADS_TO_DEGS;
            float rotY = orientation[1] * FROM_RADS_TO_DEGS;
            //Convertimos la [-180,180] grados a [0-360] grados
            if(posX<0){posX = 180+(180+posX); }
            if(rotY<0){rotY = 180+(180+rotY); }
            //Entramos una primera vez en ester if para tomar el valor de la direccion del
            //dispositivo por primera vez
            if(inicializa) {
                X_ANTERIOR = posX;
                inicializa=false;
            }
            //Comprobamos si hemos tenido que mover la imagen, y en caso
            // positivo mostramos la imagen desplazada
            boolean modif = calculateMotion(posX,rotY, 50);
            if(modif)
                gestorRutas.showImage((ImageView)findViewById(R.id.imageViewRoute),START_X,START_Y);
        }

    };

    private void setupVoice() {

        final Button boton = (Button) findViewById(R.id.volume);
        boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(volumenActivo) {
                    volumenActivo = false;
                    boton.setBackgroundResource(R.drawable.voice_off);
                }else{
                    volumenActivo = true;
                    talk();
                    boton.setBackgroundResource(R.drawable.voice_on);
                }
            }
        });
    }
}

