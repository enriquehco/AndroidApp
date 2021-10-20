package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class DisplayQRActivity extends AppCompatActivity {
    private static final String TAG = "DisplayQRActivity";

    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String token = "";
    private String tokenanterior = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.qr_read);

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        initQR();
    }

    public void initQR(){
        //Crea el detector qr
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        //Crea la camara
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
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

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

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections){
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

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
