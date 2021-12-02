package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myapplication.MESSAGE";
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeechEngine;
    private ImageView botonagente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }

        botonagente = findViewById(R.id.boton_agente);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle bundle) {
                botonagente.setImageResource((R.drawable.ic_mic_black_off3));
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String resultado = data.get(0);
                if(resultado.indexOf("1")!=-1 && resultado.indexOf("5")!=-1){
                    Intent myIntent = new Intent(MainActivity.this, DisplayRuta.class);
                    myIntent.putExtra("RouteSelected",Integer.toString(0));
                    startActivity(myIntent);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        botonagente.setOnClickListener(new View.OnClickListener() {
            int count = 0;
            @Override
            public void onClick(View v){
                if(count==0){
                    botonagente.setImageResource(R.drawable.ic_mic_black_off3_pressed);
                    speechRecognizer.startListening(speechRecognizerIntent);
                    count=1;
                }
                else {
                    botonagente.setImageResource(R.drawable.ic_mic_black_off3);
                    speechRecognizer.stopListening();
                    count = 0;
                }

            }
        });

        checkPermission();
    }

    /**Diferentes acciones según el boton que se pulse en la pantalla principal, cada uno inicia las
    actividades que son respectivamente:
    -startMap: inicia la actividad de rutas
    -startQR: inicia la actividad de lectura de código qr
    -startStaffSearch: inicia la actividad de información de departamentos
     **/

    protected void onDestroy(){
        super.onDestroy();
        speechRecognizer.destroy();;
    }

    private void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

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


}