package com.example.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/*
* Clase que gestiona las rutas disponibles asi como las distintas
* imagenes pertenecientes a cada ruta.
* De forma que se pueda iteraccionar facilmente con la seleccion de ruta imgenes, tamanios
* de las imagenes y pixels que queremos visualizar
* */
public class GestorRutas {
    //Ruta seleccionada
    private int routeselected;
    //Numero de imagen a mostrar dada una ruta
    private int numImagen;
    //PATH de la imagen que tenemos que mostrar
    private String imagenActual;
    private int numeroDeImagenes;
    private Bitmap SOURCE_BITMAP;

    /*
    * Constructor
    * */
    public GestorRutas(int ruta) {
        this.routeselected = ruta;
        this.numImagen = 0;
        this.imagenActual = "";
        this.numeroDeImagenes = 5;
    }

    /*
     * Metodo que pasa a la siguiente imagen siempre que haya mas imagenes
     * */
    public boolean SiguienteImagen(Context context, Resources resources){
        if(this.numImagen<this.numeroDeImagenes-1) {
            this.numImagen++;
            this.newImage(context, resources);
            return true;
        }
        return false;
    }

    /*
     * Metodo que pasa a la imagen anterior en caso que se pueda
     * */
    public boolean ImagenAnterior(Context context, Resources resources){
        if(this.numImagen>0) {
            this.numImagen--;
            this.newImage(context, resources);
            return true;
        }
        return false;

    }

    /*
     * Metodo que genera el path de imagen actual en funcion de la ruta elegida y el numero
     * de imagen de esa ruta y genera el bitmap de dicha imagen
     * */
    public void newImage(Context context, Resources resources){
        imagenActual = "drawable/ruta"+routeselected+"_"+numImagen;
        //Context context = getApplicationContext();
        int id = context.getResources().getIdentifier(imagenActual, null, context.getPackageName());
        //SOURCE_BITMAP = BitmapFactory.decodeResource(getResources(), id);
        SOURCE_BITMAP = BitmapFactory.decodeResource(resources, id);
    }

    /*
     * Metodo que muestra la imagen cargada en el Bitmap en la ImageView del layout
     * Muestra solo la parte de la imagen comprendida entre
     * START_X y START_X+WIDTH_PX
     * START_Y y START_Y+WIDTH_PY
     * */
    public void showImage(ImageView image,int START_X,int START_Y,int WIDTH_PX, int HEIGHT_PX){
        if(START_X<0){START_X=0;}
        if(START_X+WIDTH_PX>4000){START_X=4000-WIDTH_PX;}

        Bitmap newBitmap = Bitmap.createBitmap(SOURCE_BITMAP, START_X, START_Y, WIDTH_PX, HEIGHT_PX, null, false);
        //ImageView image = (ImageView)findViewById(R.id.imageViewRoute);
        image.setImageBitmap(newBitmap);
    }
}