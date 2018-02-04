package com.example.juliette.dchiffrage;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Partition {
    String nom;
    private ArrayList<Bitmap> mesures;
    public Partition(String m, ArrayList<Bitmap> L) {
        nom = m;
        mesures = L ;
    }
}
