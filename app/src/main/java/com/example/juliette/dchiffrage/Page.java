package com.example.juliette.dchiffrage;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Page {
    Bitmap btm;
    ArrayList<int[]> mesures;

    public Page(Bitmap btm, ArrayList<int[]> mesures) {
        this.btm = btm;
        this.mesures=mesures;
    }
}
