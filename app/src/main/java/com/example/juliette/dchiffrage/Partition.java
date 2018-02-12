package com.example.juliette.dchiffrage;

import android.graphics.Bitmap;
import android.support.constraint.solver.widgets.Rectangle;

import java.util.ArrayList;

public class Partition {
    String nom;
    ArrayList<Page> pages;
    public Partition(String m, ArrayList<Page> L) {
        nom = m;
        pages = L ;
    }
    public int nbMesures() {
        int nb = 0;
        for(Page page:pages) nb += page.mesures.size();
        return nb;
    }
}
