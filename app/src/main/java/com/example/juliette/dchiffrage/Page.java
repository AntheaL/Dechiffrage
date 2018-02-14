package com.example.juliette.dchiffrage;

import android.graphics.Bitmap;
import android.support.constraint.solver.widgets.Rectangle;

import java.util.ArrayList;

public class Page {
    Bitmap btm;
    ArrayList<Rectangle> mesures;

    public Page(Bitmap btm, ArrayList<Rectangle> mesures) {
        this.btm = btm;
        this.mesures=mesures;
    }
}
