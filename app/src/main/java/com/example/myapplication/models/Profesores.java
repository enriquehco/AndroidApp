package com.example.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Profesores implements Parcelable {
    private int imagen;
    private String nombre;
    private String correo;
    private int despacho;
    private String addinfo;

    public Profesores(int imagen, String nombre, String correo, int despacho, String addinfo) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.correo = correo;
        this.despacho = despacho;
        this.addinfo = addinfo;
    }

    public Profesores() {
    }

    protected Profesores(Parcel in) {
        imagen = in.readInt();
        nombre = in.readString();
        correo = in.readString();
        despacho = in.readInt();
        addinfo = in.readString();
    }

    public static final Creator<Profesores> CREATOR = new Creator<Profesores>() {
        @Override
        public Profesores createFromParcel(Parcel in) {
            return new Profesores(in);
        }

        @Override
        public Profesores[] newArray(int size) {
            return new Profesores[size];
        }
    };

    public int getImagen() {
        return imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public int getDespacho() {
        return despacho;
    }

    public String getAddinfo() {
        return addinfo;
    }

    public void setImagen(int imagen){
        this.imagen = imagen;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setDespacho(int despacho) {
        this.despacho = despacho;
    }

    public void setAddinfo(String addinfo) {
        this.addinfo = addinfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imagen);
        dest.writeString(nombre);
        dest.writeString(correo);
        dest.writeInt(despacho);
        dest.writeString(addinfo);
    }
}
