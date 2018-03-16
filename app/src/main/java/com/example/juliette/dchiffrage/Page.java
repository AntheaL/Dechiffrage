package com.example.juliette.dchiffrage;

import android.graphics.Rect;

import java.util.ArrayList;

public class Page {
    String path;
    ArrayList<Rect> mesures;

    public Page(String path, ArrayList<Rect> mesures) {
        this.path = path;
        this.mesures=mesures;
    }
}
