package com.example.juliette.dchiffrage;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Partition {
    String nom;
    private ArrayList<Page> pages;
    public Partition(String m, ArrayList<Page> L) {
        nom = m;
        pages = L ;
    }
}
