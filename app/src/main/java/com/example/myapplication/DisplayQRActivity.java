package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class DisplayQRActivity extends AppCompatActivity {

    private static final String TAG = "DisplayQRActivity";

    //Parámetros y permisos para la camara
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String token = "";
    private String tokenanterior = "";

    //onCreate se activa cuando se crea la actividad, inicializa lo indispensable para que funcione
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //parametro especial para indicar que sea una actividad a pantalla completa (para mas
        //immersion en la cámara qr)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.qr_read);

        //Inicializa el surface donde se verá la camara, así como el proceso propio de la cámara,
        //y el boton que mostrará la ayuda (top-right on screen)
        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        initQR();
        setupHelp();
    }

    //Metodo que inicializa el mensaje de ayuda
    private void setupHelp() {
        Button btn = (Button) findViewById(R.id.fbutton);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                DisplayHelp dialog = new DisplayHelp();
                dialog.show(manager, "MSG");
            }
        });
    }


    public void initQR(){
        //Crea el detector qr
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        //Crea la camara y la inicializa
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(1600,1024).setAutoFocusEnabled(true).build();

        //listener de la camara
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                //verifica si hay permisos de camara
                if(ActivityCompat.checkSelfPermission(DisplayQRActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        //vemos que la versión de android es al menos M para mostrar el dialogo de solicitud de cámara
                        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA));
                            requestPermissions(new String[]{Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    return;
                } else {
                    //inicializa la cámara en la vista cameraView definida anteriormente
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            //método vacio pero necesario para la definición del holder
            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            //método destroyer que detiene la actividad de cámara para que no siga usando recursos
            //tras su detención
            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        //se prepara el lector qr
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            //método que procesa la información recibida por el lector
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections){
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                //si se ha detectado un barcode/qr/else.
                if(barcodes.size() > 0) {
                    //obtener el token
                    token = barcodes.valueAt(0).displayValue.toString();

                    //ahora vemos que el token no sea igual que el anterior, para no ciclar indefinidamente
                    if(!token.equals(tokenanterior)) {
                        tokenanterior = token;
                        Log.i("token", token);

                        if(URLUtil.isValidUrl(token)){
                            //si es una url valida, abre internet
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(token));
                            startActivity(browserIntent);
                        } else {
                            //comparte en otras apps
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_TEXT,token);
                            shareIntent.setType("text/plain");
                            startActivity(shareIntent);
                        }

                        //espera de 5 segundos (para que sea efectiva la obtención y procesado del
                        //token) y luego limpia dicho token por si se desean observar nuevos.
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    synchronized (this) {
                                        wait(5000);
                                        //se limpia el token
                                        tokenanterior="";
                                    }
                                } catch (InterruptedException e){
                                    Log.e("ERROR", "Waiting didn't work!");
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }

        });
    }
}
