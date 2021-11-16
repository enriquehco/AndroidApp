package com.example.myapplication;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.resources.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DisplayBuscaPersonal extends AppCompatActivity {
    private static final String TAG = "DisplayBuscaPersonal";
    //creacion del sensor y manager de sensor
    private SensorManager sensorManager;
    private Sensor accelerometer;

    //otros parámetros de movimiento
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    private long lastUpdate = -1;

    //Inicializar el tablayout y viewpager para poder usar una vista de varias páginas (departamentos)
    TabLayout tabLayout;
    ViewPager2 viewPager2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_display_departamentos);

        //inicializar tablayout y viewpager a sus respectivas vistas
        tabLayout = findViewById(R.id.dep_tab);
        viewPager2 = findViewById(R.id.dep_pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        //añadir los tabs al mediador que los controla
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText("Tab " + (position + 1));
            }
        }).attach();

        //inicializar sus títulos según nombre de departamento
        tabLayout.getTabAt(0).setText("Departamento IA");
        tabLayout.getTabAt(1).setText("Departamento Redes");
        tabLayout.getTabAt(2).setText("Departamento Software");

        //inicializamos los datos de los sensores
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        lastUpdate = -1;
        sensorManager.registerListener(movementSensorEventListener, accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }

    //Default para reiniciar el sensor si se resume la actividad una vez interrumpida (apagar la pantalla / pasar a otra aplicación / etc)
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(movementSensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    //Default para pausar el uso del sensor si la aplicación padre pausa (evita que el sensor se use fuera del contexto para el que está definido)
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(movementSensorEventListener);
    }

    //Este snippet declara un SensorEventListener que se encarga de registrar cambios significativos
    //en el sensor y tomar acción según el movimiento que represente
    SensorEventListener movementSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                //obtener valores del sensor en dicho momento
                mGravity = event.values.clone();
                float x = mGravity[0];
                float y = mGravity[1];
                float z = mGravity[2];
                mAccelLast = mAccelCurrent;
                mAccelCurrent = (float) x;
                float delta = mAccelCurrent - mAccelLast;
                mAccel = mAccel*0.9f + delta;
                //tocar la linea anterior si queremos más o menos sensibilidad

                //parámetro para conocer en que tab estamos situados y hacer cálculos sobre
                //la siguiente tab a alcanzar
                int tabactiva = tabLayout.getSelectedTabPosition();

                //Si el movimiento que se detecta en x es mas grande que 5, se asigna a un movimiento a la izquierda, por tanto a pasar a la página de la izquierda
                if(Round(x,4)>5.0000){
                    int tabantigua = tabactiva;
                    tabactiva = ((tabactiva - 1) % 3 + 3)%3;

                    //Selecciona la tab a la izquierda de la actual (si la tab es la que está mas a
                    // la izquierda entonces hace wrap-around a la última
                    tabLayout.selectTab((tabLayout.getTabAt(tabactiva)));
                    Log.i("myactivity", "supuesto gesto izquierda = x:" + Round(x,4) + ", y:" + Round(y,4) + ", z:" + Round(z,4));
                    Log.i("myactivity", "paso de tab: " + tabantigua + " a tab: " + tabactiva);

                    //duerme durante unos ms para que no detecte el movimiento de retorno
                    try{
                        Thread.sleep(100);
                    }catch(InterruptedException ex){
                        Thread.currentThread().interrupt();
                    }
                }
                //Si el movimiento que se detecta en x es mas pequeño que -5, se asigna a un movimiento a la derecha, por tanto a pasar a la página de la derecha
                else if(Round(x,4)<-5.0000){
                    tabactiva = (tabactiva + 1) % 3;

                    //Selecciona la tab a la derecha de la actual (si la tab es la que está mas a la
                    //derecha, entonces hace wrap-around a la primera
                    tabLayout.selectTab((tabLayout.getTabAt(tabactiva)));
                    Log.i("myactivity", "supuesto gesto derecha = x:" + Round(x,4) + ", y:" + Round(y,4) + ", z:" + Round(z,4));

                    //duerme durante unos ms para que no detecte el movimiento de retorno
                    // (retocar, hay veces que sigue pillando retorno o dos desplazamientos)
                    try{
                        Thread.sleep(100);
                    }catch(InterruptedException ex){
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //método (requerido) vacio por si cambiase la accuracy del sensor
        }
    };

    //Funcion de redondeo para los datos de movimiento del sensor
    public static float Round(float Rval, int Rpl) {
        float p = (float)Math.pow(10,Rpl);
        Rval = Rval * p;
        float tmp = Math.round(Rval);
        return (float)tmp/p;
    }


}
