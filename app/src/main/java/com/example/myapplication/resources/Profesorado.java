package com.example.myapplication.resources;

import com.example.myapplication.R;
import com.example.myapplication.models.Profesores;

public class Profesorado {
    public static Profesores[] getProfesoresIA(){
        return PROFESORESIA;
    }

    public static final Profesores JABAD = new Profesores(R.drawable.jabad, "Francisco Javier Abad", "jabad@ugr.es", 203, "Imparte las asignaturas de: Metodología de la Programación");
    public static final Profesores SILVIAAC= new Profesores(R.drawable.silviaac, "Silvia Acid Carrillo", "acid@decsai.ugr.es", 304, "imparte las asignaturas de: Metodología de la programación");

    public static final Profesores[] PROFESORESIA = {JABAD,SILVIAAC};
}
