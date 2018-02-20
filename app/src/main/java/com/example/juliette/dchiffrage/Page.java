package com.example.juliette.dchiffrage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.solver.widgets.Rectangle;

import java.io.File;
import java.util.ArrayList;

public class Page {
    String path;
    ArrayList<Rectangle> mesures;

    public Page(String path, ArrayList<Rectangle> mesures) {
        this.path = path;
        this.mesures=mesures;
    }
}
